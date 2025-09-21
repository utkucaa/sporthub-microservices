package com.sporthub.notification.kafka;

import com.sporthub.notification.dto.event.NotificationSentEvent;
import com.sporthub.notification.dto.event.NotificationFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventProducer.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // bildirim gönderildi event'ini yayınla
    public void publishNotificationSent(Long reservationId, Long userId, String notificationType) {
        try {
            NotificationSentEvent event = new NotificationSentEvent(
                reservationId,
                userId,
                notificationType,
                LocalDateTime.now()
            );

            kafkaTemplate.send("notification-sent", event);
            logger.info("Published notification-sent event for reservation: {}, user: {}, type: {}", 
                       reservationId, userId, notificationType);

        } catch (Exception e) {
            logger.error("Failed to publish notification-sent event", e);
        }
    }

    // bildirim başarısız event'ini yayınla
    public void publishNotificationFailed(Long reservationId, Long userId, String notificationType, String reason) {
        try {
            NotificationFailedEvent event = new NotificationFailedEvent(
                reservationId,
                userId,
                notificationType,
                reason,
                LocalDateTime.now()
            );

            kafkaTemplate.send("notification-failed", event);
            logger.info("Published notification-failed event for reservation: {}, user: {}, type: {}, reason: {}", 
                       reservationId, userId, notificationType, reason);

        } catch (Exception e) {
            logger.error("Failed to publish notification-failed event", e);
        }
    }

    // email teslim edildi event'ini yayınla
    public void publishEmailDelivered(Long notificationId, String email) {
        try {
            kafkaTemplate.send("email-delivered", 
                "Email delivered to: " + email + ", notification: " + notificationId);
            
            logger.info("Published email-delivered event for notification: {}, email: {}", 
                       notificationId, email);

        } catch (Exception e) {
            logger.error("Failed to publish email-delivered event", e);
        }
    }

    // sms teslim edildi event'ini yayınla
    public void publishSmsDelivered(Long notificationId, String phoneNumber) {
        try {
            kafkaTemplate.send("sms-delivered", 
                "SMS delivered to: " + phoneNumber + ", notification: " + notificationId);
            
            logger.info("Published sms-delivered event for notification: {}, phone: {}", 
                       notificationId, phoneNumber);

        } catch (Exception e) {
            logger.error("Failed to publish sms-delivered event", e);
        }
    }
}
