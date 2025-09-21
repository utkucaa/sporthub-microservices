package com.sporthub.notification.scheduler;

import com.sporthub.notification.client.CourtServiceClient;
import com.sporthub.notification.client.ReservationServiceClient;
import com.sporthub.notification.client.UserServiceClient;
import com.sporthub.notification.dto.external.CourtResponse;
import com.sporthub.notification.dto.external.ReservationResponse;
import com.sporthub.notification.dto.external.UserProfileResponse;
import com.sporthub.notification.entity.Notification;
import com.sporthub.notification.entity.NotificationSchedule;
import com.sporthub.notification.enums.NotificationStatus;
import com.sporthub.notification.repository.NotificationRepository;
import com.sporthub.notification.service.EmailService;
import com.sporthub.notification.service.NotificationSchedulerService;
import com.sporthub.notification.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class NotificationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);

    @Autowired
    private NotificationSchedulerService schedulerService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private ReservationServiceClient reservationServiceClient;

    @Autowired
    private CourtServiceClient courtServiceClient;

    @Value("${notification.cleanup.retention-days:90}")
    private int retentionDays;

    // her dakika çalışır - zamanı gelen hatırlatmaları gönderir
    @Scheduled(fixedRate = 60000) 
    public void processScheduledReminders() {
        try {
            List<NotificationSchedule> dueSchedules = schedulerService.getScheduledReminders();
            
            if (!dueSchedules.isEmpty()) {
                logger.info("Processing {} scheduled reminders", dueSchedules.size());
                
                for (NotificationSchedule schedule : dueSchedules) {
                    processScheduledReminder(schedule);
                }
            }

        } catch (Exception e) {
            logger.error("Error processing scheduled reminders", e);
        }
    }

    // 5dk'da bir çalışır - başarısız bildirimleri tekrar dener
    @Scheduled(fixedRate = 300000) 
    public void retryFailedNotifications() {
        try {
            List<Notification> failedNotifications = notificationRepository
                .findByStatusAndRetryCountLessThan(NotificationStatus.FAILED, 3);

            if (!failedNotifications.isEmpty()) {
                logger.info("Retrying {} failed notifications", failedNotifications.size());
                
                for (Notification notification : failedNotifications) {
                    retryFailedNotification(notification);
                }
            }

        } catch (Exception e) {
            logger.error("Error retrying failed notifications", e);
        }
    }

    // gece 2'de çalışır - eski bildirimleri siler
    @Scheduled(cron = "0 0 2 * * ?") 
    public void cleanupOldNotifications() {
        logger.info("Cleaning up old notifications...");

        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
            List<Notification> oldNotifications = notificationRepository.findByCreatedAtBefore(cutoffDate);

            if (!oldNotifications.isEmpty()) {
                notificationRepository.deleteAll(oldNotifications);
                logger.info("Cleaned up {} old notifications", oldNotifications.size());
            }

            schedulerService.cleanupOldSchedules(retentionDays);

        } catch (Exception e) {
            logger.error("Error cleaning up old notifications", e);
        }
    }

    // saatte bir çalışır - başarısız hatırlatmaları yeniden planlar
    @Scheduled(cron = "0 0 * * * ?") 
    public void rescheduleFailedReminders() {
        try {
            schedulerService.rescheduleFailedReminders();
        } catch (Exception e) {
            logger.error("Error rescheduling failed reminders", e);
        }
    }

    // 10dk'da bir çalışır - sistem sağlığını kontrol eder
    @Scheduled(fixedRate = 600000) 
    public void healthCheck() {
        try {
            List<Notification> pendingNotifications = notificationRepository.findByStatus(NotificationStatus.PENDING);
            
            if (pendingNotifications.size() > 100) {
                logger.warn("High number of pending notifications: {}", pendingNotifications.size());
            }

            List<NotificationSchedule> pendingSchedules = schedulerService.getScheduledReminders();
            
            if (pendingSchedules.size() > 50) {
                logger.warn("High number of pending schedules: {}", pendingSchedules.size());
            }

        } catch (Exception e) {
            logger.error("Error during health check", e);
        }
    }

    private void processScheduledReminder(NotificationSchedule schedule) {
        try {
            logger.info("Processing scheduled reminder: {}", schedule.getId());

            UserProfileResponse userProfile = userServiceClient.getUserProfile(schedule.getUserId());
            ReservationResponse reservation = reservationServiceClient.getReservation(schedule.getReservationId());
            CourtResponse court = courtServiceClient.getCourt(reservation.getCourtId());

            String startTime = reservation.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            String endTime = reservation.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

            boolean sent = false;

            switch (schedule.getNotificationType()) {
                case EMAIL:
                    sent = emailService.sendReminderEmail(
                        schedule, 
                        userProfile.getEmail(), 
                        court.getName(), 
                        startTime, 
                        endTime
                    );
                    break;
                case SMS:
                    sent = smsService.sendReservationReminderSms(
                        userProfile.getPhoneNumber(),
                        court.getName(),
                        startTime,
                        endTime
                    );
                    break;
                default:
                    logger.warn("Unsupported notification type for reminder: {}", schedule.getNotificationType());
                    break;
            }

            if (sent) {
                schedulerService.updateScheduleStatus(schedule.getId(), NotificationStatus.SENT, null);
                logger.info("Reminder sent successfully: {}", schedule.getId());
            } else {
                schedulerService.updateScheduleStatus(schedule.getId(), NotificationStatus.FAILED, "Failed to send reminder");
                logger.error("Failed to send reminder: {}", schedule.getId());
            }

        } catch (Exception e) {
            logger.error("Error processing scheduled reminder: {}", schedule.getId(), e);
            schedulerService.updateScheduleStatus(schedule.getId(), NotificationStatus.FAILED, e.getMessage());
        }
    }

    private void retryFailedNotification(Notification notification) {
        try {
            logger.info("Retrying failed notification: {}", notification.getId());

            boolean sent = false;

            switch (notification.getType()) {
                case EMAIL:
                    sent = emailService.sendEmail(
                        notification.getRecipientEmail(),
                        notification.getTitle(),
                        notification.getContent(),
                        null
                    );
                    break;
                case SMS:
                    sent = smsService.sendSms(
                        notification.getRecipientPhone(),
                        notification.getContent()
                    );
                    break;
                default:
                    logger.warn("Unsupported notification type for retry: {}", notification.getType());
                    break;
            }

            notification.setRetryCount(notification.getRetryCount() + 1);

            if (sent) {
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                notification.setFailureReason(null);
                logger.info("Notification retry successful: {}", notification.getId());
            } else {
                notification.setFailureReason("Retry failed");
                logger.warn("Notification retry failed: {}", notification.getId());
            }

            notificationRepository.save(notification);

        } catch (Exception e) {
            logger.error("Error retrying notification: {}", notification.getId(), e);
            notification.setRetryCount(notification.getRetryCount() + 1);
            notification.setFailureReason(e.getMessage());
            notificationRepository.save(notification);
        }
    }
}
