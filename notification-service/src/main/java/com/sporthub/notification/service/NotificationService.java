package com.sporthub.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporthub.notification.dto.request.BulkNotificationRequest;
import com.sporthub.notification.dto.request.ScheduleReminderRequest;
import com.sporthub.notification.dto.request.SendNotificationRequest;
import com.sporthub.notification.dto.response.NotificationResponse;
import com.sporthub.notification.entity.Notification;
import com.sporthub.notification.entity.NotificationSchedule;
import com.sporthub.notification.enums.NotificationStatus;
import com.sporthub.notification.enums.NotificationType;
import com.sporthub.notification.enums.ReminderType;
import com.sporthub.notification.repository.NotificationRepository;
import com.sporthub.notification.repository.NotificationScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationScheduleRepository scheduleRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private NotificationPreferencesService preferencesService;

    @Autowired
    private ObjectMapper objectMapper;

    // bildirim gönder - ana metod
    public NotificationResponse sendNotification(SendNotificationRequest request) {
        logger.info("Sending notification to user: {}, type: {}", request.getUserId(), request.getType());

        try {
            if (!preferencesService.isNotificationAllowed(request.getUserId(), request.getCategory(), request.getType())) {
                logger.warn("Notification not allowed for user: {}, category: {}, type: {}", 
                           request.getUserId(), request.getCategory(), request.getType());
                return null;
            }

            Notification notification = createNotificationEntity(request);
            
            if (request.getMetadata() != null) {
                notification.setMetadata(objectMapper.writeValueAsString(request.getMetadata()));
            }

            notification = notificationRepository.save(notification);

            boolean sent = sendNotificationByType(notification);
            
            if (sent) {
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                logger.info("Notification sent successfully: {}", notification.getId());
            } else {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setFailureReason("Failed to send notification");
                logger.error("Failed to send notification: {}", notification.getId());
            }

            notification = notificationRepository.save(notification);
            return mapToResponse(notification);

        } catch (Exception e) {
            logger.error("Error sending notification", e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }

    // toplu bildirim gönder
    public void sendBulkNotification(BulkNotificationRequest request) {
        logger.info("Sending bulk notification to {} users", request.getUserIds().size());

        for (Long userId : request.getUserIds()) {
            SendNotificationRequest singleRequest = new SendNotificationRequest(
                userId, request.getType(), request.getCategory(), 
                request.getTitle(), request.getContent()
            );
            singleRequest.setMetadata(request.getMetadata());
            
            try {
                sendNotification(singleRequest);
            } catch (Exception e) {
                logger.error("Failed to send notification to user: {}", userId, e);
            }
        }
    }

    // hatırlatma planla
    public void scheduleReminder(ScheduleReminderRequest request, LocalDateTime reservationStartTime) {
        logger.info("Scheduling reminders for reservation: {}", request.getReservationId());

        for (ReminderType reminderType : request.getReminderTypes()) {
            LocalDateTime scheduledTime = reservationStartTime.minusMinutes(reminderType.getMinutesBefore());
            
            NotificationSchedule schedule = new NotificationSchedule(
                request.getReservationId(),
                request.getUserId(),
                NotificationType.EMAIL, // Varsayılan olarak email
                reminderType,
                scheduledTime
            );

            scheduleRepository.save(schedule);
            logger.info("Reminder scheduled for reservation: {}, type: {}, time: {}", 
                       request.getReservationId(), reminderType, scheduledTime);
        }
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationHistory(Long userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(this::mapToResponse);
    }

    public void updateNotificationStatus(Long notificationId, NotificationStatus status) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setStatus(status);
            
            if (status == NotificationStatus.DELIVERED) {
                notification.setDeliveredAt(LocalDateTime.now());
            } else if (status == NotificationStatus.READ) {
                notification.setReadAt(LocalDateTime.now());
            }
            
            notificationRepository.save(notification);
            logger.info("Notification status updated: {}, status: {}", notificationId, status);
        }
    }

    // başarısız bildirimleri tekrar dene
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository
            .findByStatusAndRetryCountLessThan(NotificationStatus.FAILED, 3);

        logger.info("Retrying {} failed notifications", failedNotifications.size());

        for (Notification notification : failedNotifications) {
            try {
                boolean sent = sendNotificationByType(notification);
                notification.setRetryCount(notification.getRetryCount() + 1);
                
                if (sent) {
                    notification.setStatus(NotificationStatus.SENT);
                    notification.setSentAt(LocalDateTime.now());
                    logger.info("Retry successful for notification: {}", notification.getId());
                } else {
                    notification.setFailureReason("Retry failed");
                    logger.warn("Retry failed for notification: {}", notification.getId());
                }
                
                notificationRepository.save(notification);
                
            } catch (Exception e) {
                logger.error("Error retrying notification: {}", notification.getId(), e);
            }
        }
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.SENT);
    }

    private Notification createNotificationEntity(SendNotificationRequest request) {
        Notification notification = new Notification(
            request.getUserId(),
            request.getType(),
            request.getCategory(),
            request.getTitle(),
            request.getContent()
        );

        notification.setRecipientEmail(request.getRecipientEmail());
        notification.setRecipientPhone(request.getRecipientPhone());
        
        return notification;
    }

    private boolean sendNotificationByType(Notification notification) {
        try {
            switch (notification.getType()) {
                case EMAIL:
                    return emailService.sendEmail(
                        notification.getRecipientEmail(),
                        notification.getTitle(),
                        notification.getContent(),
                        null
                    );
                case SMS:
                    return smsService.sendSms(
                        notification.getRecipientPhone(),
                        notification.getContent()
                    );
                case PUSH:
                    return pushNotificationService.sendPushNotification(
                        null, // Device token should be retrieved from user service
                        notification.getTitle(),
                        notification.getContent(),
                        null
                    );
                case IN_APP:
                    return true;
                default:
                    return false;
            }
        } catch (Exception e) {
            logger.error("Error sending notification by type: {}", notification.getType(), e);
            return false;
        }
    }

    private NotificationResponse mapToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setUserId(notification.getUserId());
        response.setType(notification.getType());
        response.setCategory(notification.getCategory());
        response.setTitle(notification.getTitle());
        response.setContent(notification.getContent());
        response.setRecipientEmail(notification.getRecipientEmail());
        response.setRecipientPhone(notification.getRecipientPhone());
        response.setStatus(notification.getStatus());
        response.setSentAt(notification.getSentAt());
        response.setDeliveredAt(notification.getDeliveredAt());
        response.setReadAt(notification.getReadAt());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
}
