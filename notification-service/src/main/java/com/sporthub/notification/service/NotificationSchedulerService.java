package com.sporthub.notification.service;

import com.sporthub.notification.dto.event.ReservationCreatedEvent;
import com.sporthub.notification.entity.NotificationSchedule;
import com.sporthub.notification.enums.NotificationStatus;
import com.sporthub.notification.enums.NotificationType;
import com.sporthub.notification.enums.ReminderType;
import com.sporthub.notification.repository.NotificationScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationSchedulerService.class);

    @Autowired
    private NotificationScheduleRepository scheduleRepository;

    @Autowired
    private NotificationPreferencesService preferencesService;

    // rezervasyon hatırlatmalarını planla
    public void scheduleReservationReminders(ReservationCreatedEvent event) {
        logger.info("Scheduling reservation reminders for reservation: {}", event.getReservationId());

        if (!preferencesService.isNotificationAllowed(event.getUserId(), 
                com.sporthub.notification.enums.NotificationCategory.RESERVATION_REMINDER, 
                NotificationType.EMAIL)) {
            logger.info("User {} has disabled reservation reminders", event.getUserId());
            return;
        }

        ReminderType[] reminderTypes = {ReminderType.REMINDER_24H, ReminderType.REMINDER_1H, ReminderType.REMINDER_30M};

        for (ReminderType reminderType : reminderTypes) {
            LocalDateTime scheduledTime = event.getStartTime().minusMinutes(reminderType.getMinutesBefore());
            
            if (scheduledTime.isBefore(LocalDateTime.now())) {
                logger.warn("Skipping past reminder time for reservation: {}, type: {}", 
                           event.getReservationId(), reminderType);
                continue;
            }

            NotificationSchedule schedule = new NotificationSchedule(
                event.getReservationId(),
                event.getUserId(),
                NotificationType.EMAIL,
                reminderType,
                scheduledTime
            );

            scheduleRepository.save(schedule);
            logger.info("Reminder scheduled for reservation: {}, type: {}, time: {}", 
                       event.getReservationId(), reminderType, scheduledTime);
        }
    }

    // planlanmış hatırlatmaları iptal et
    public void cancelScheduledReminders(Long reservationId) {
        logger.info("Cancelling scheduled reminders for reservation: {}", reservationId);

        List<NotificationSchedule> pendingSchedules = scheduleRepository
            .findByReservationIdAndStatus(reservationId, NotificationStatus.PENDING);

        for (NotificationSchedule schedule : pendingSchedules) {
            schedule.setStatus(NotificationStatus.FAILED);
            schedule.setFailureReason("Reservation cancelled");
            scheduleRepository.save(schedule);
        }

        logger.info("Cancelled {} reminders for reservation: {}", pendingSchedules.size(), reservationId);
    }

    @Transactional(readOnly = true)
    public List<NotificationSchedule> getScheduledReminders() {
        LocalDateTime now = LocalDateTime.now();
        return scheduleRepository.findDueSchedules(now, NotificationStatus.PENDING);
    }

    public void updateScheduleStatus(Long scheduleId, NotificationStatus status, String failureReason) {
        scheduleRepository.findById(scheduleId).ifPresent(schedule -> {
            schedule.setStatus(status);
            if (status == NotificationStatus.SENT) {
                schedule.setSentAt(LocalDateTime.now());
            }
            if (failureReason != null) {
                schedule.setFailureReason(failureReason);
            }
            scheduleRepository.save(schedule);
        });
    }

    // başarısız hatırlatmaları yeniden planla
    public void rescheduleFailedReminders() {
        logger.info("Rescheduling failed reminders");

        List<NotificationSchedule> failedSchedules = scheduleRepository.findByStatus(NotificationStatus.FAILED);
        
        for (NotificationSchedule schedule : failedSchedules) {
            if (schedule.getScheduledTime().isAfter(LocalDateTime.now().minusHours(1))) {
                schedule.setStatus(NotificationStatus.PENDING);
                schedule.setFailureReason(null);
                schedule.setScheduledTime(LocalDateTime.now().plusMinutes(5));
                scheduleRepository.save(schedule);
                
                logger.info("Rescheduled failed reminder: {}", schedule.getId());
            }
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationSchedule> getUserSchedules(Long userId) {
        return scheduleRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<NotificationSchedule> getReservationSchedules(Long reservationId) {
        return scheduleRepository.findByReservationId(reservationId);
    }

    @Transactional(readOnly = true)
    public List<NotificationSchedule> getSchedulesBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return scheduleRepository.findByScheduledTimeBetween(startTime, endTime);
    }

    // eski schedule'ları temizle
    public void cleanupOldSchedules(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        
        List<NotificationSchedule> oldSchedules = scheduleRepository.findAll().stream()
            .filter(schedule -> schedule.getCreatedAt().isBefore(cutoffDate))
            .filter(schedule -> schedule.getStatus() == NotificationStatus.SENT || 
                              schedule.getStatus() == NotificationStatus.FAILED)
            .toList();

        if (!oldSchedules.isEmpty()) {
            scheduleRepository.deleteAll(oldSchedules);
            logger.info("Cleaned up {} old schedules", oldSchedules.size());
        }
    }
}
