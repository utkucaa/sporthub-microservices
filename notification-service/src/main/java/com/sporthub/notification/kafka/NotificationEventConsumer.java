package com.sporthub.notification.kafka;

import com.sporthub.notification.client.CourtServiceClient;
import com.sporthub.notification.client.UserServiceClient;
import com.sporthub.notification.dto.event.PaymentCompletedEvent;
import com.sporthub.notification.dto.event.ReservationCreatedEvent;
import com.sporthub.notification.dto.event.UserRegisteredEvent;
import com.sporthub.notification.dto.external.CourtResponse;
import com.sporthub.notification.dto.external.UserProfileResponse;
import com.sporthub.notification.service.EmailService;
import com.sporthub.notification.service.NotificationPreferencesService;
import com.sporthub.notification.service.NotificationSchedulerService;
import com.sporthub.notification.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class NotificationEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventConsumer.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private NotificationSchedulerService schedulerService;

    @Autowired
    private NotificationPreferencesService preferencesService;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private CourtServiceClient courtServiceClient;

    @Autowired
    private NotificationEventProducer eventProducer;

    // rezervasyon oluşturuldu event'ini dinler
    @KafkaListener(topics = "reservation-created", groupId = "notification-service")
    public void handleReservationCreated(ReservationCreatedEvent event) {
        logger.info("Received reservation-created event for reservation: {}", event.getReservationId());

        try {
            UserProfileResponse userProfile = userServiceClient.getUserProfile(event.getUserId());
            
            CourtResponse court = courtServiceClient.getCourt(event.getCourtId());

            boolean emailSent = emailService.sendReservationConfirmationEmail(event, userProfile.getEmail());
            
            if (emailSent) {
                logger.info("Reservation confirmation email sent for reservation: {}", event.getReservationId());
                
                if (preferencesService.isNotificationAllowed(event.getUserId(), 
                        com.sporthub.notification.enums.NotificationCategory.RESERVATION_CONFIRMATION, 
                        com.sporthub.notification.enums.NotificationType.SMS)) {
                    
                    String date = event.getStartTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    String startTime = event.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                    String endTime = event.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                    
                    smsService.sendReservationConfirmationSms(
                        userProfile.getPhoneNumber(),
                        court.getName(),
                        date,
                        startTime,
                        endTime,
                        event.getReservationId()
                    );
                }
                
                // Hatırlatmaları schedule et
                schedulerService.scheduleReservationReminders(event);
                
                // Success event publish et
                eventProducer.publishNotificationSent(
                    event.getReservationId(), 
                    event.getUserId(), 
                    "RESERVATION_CONFIRMATION"
                );
            } else {
                // Failure event publish et
                eventProducer.publishNotificationFailed(
                    event.getReservationId(), 
                    event.getUserId(), 
                    "RESERVATION_CONFIRMATION", 
                    "Email sending failed"
                );
            }

        } catch (Exception e) {
            logger.error("Error handling reservation-created event for reservation: {}", event.getReservationId(), e);
            eventProducer.publishNotificationFailed(
                event.getReservationId(), 
                event.getUserId(), 
                "RESERVATION_CONFIRMATION", 
                e.getMessage()
            );
        }
    }

    // ödeme tamamlandı event'ini dinler
    @KafkaListener(topics = "payment-completed", groupId = "notification-service")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        logger.info("Received payment-completed event for payment: {}", event.getPaymentId());

        try {
            UserProfileResponse userProfile = userServiceClient.getUserProfile(event.getUserId());

            // Ödeme onay emaili gönder
            boolean emailSent = emailService.sendPaymentConfirmationEmail(event, userProfile.getEmail());
            
            if (emailSent) {
                logger.info("Payment confirmation email sent for payment: {}", event.getPaymentId());
                
                if (preferencesService.isNotificationAllowed(event.getUserId(), 
                        com.sporthub.notification.enums.NotificationCategory.PAYMENT_CONFIRMATION, 
                        com.sporthub.notification.enums.NotificationType.SMS)) {
                    
                    smsService.sendPaymentConfirmationSms(
                        userProfile.getPhoneNumber(),
                        event.getAmount().toString(),
                        event.getReservationId()
                    );
                }
                
                // Success event publish et
                eventProducer.publishNotificationSent(
                    event.getReservationId(), 
                    event.getUserId(), 
                    "PAYMENT_CONFIRMATION"
                );
            } else {
                // Failure event publish et
                eventProducer.publishNotificationFailed(
                    event.getReservationId(), 
                    event.getUserId(), 
                    "PAYMENT_CONFIRMATION", 
                    "Email sending failed"
                );
            }

        } catch (Exception e) {
            logger.error("Error handling payment-completed event for payment: {}", event.getPaymentId(), e);
            eventProducer.publishNotificationFailed(
                event.getReservationId(), 
                event.getUserId(), 
                "PAYMENT_CONFIRMATION", 
                e.getMessage()
            );
        }
    }

    // rezervasyon iptal event'ini dinler
    @KafkaListener(topics = "reservation-cancelled", groupId = "notification-service")
    public void handleReservationCancelled(ReservationCreatedEvent event) {
        logger.info("Received reservation-cancelled event for reservation: {}", event.getReservationId());

        try {
            UserProfileResponse userProfile = userServiceClient.getUserProfile(event.getUserId());
            
            CourtResponse court = courtServiceClient.getCourt(event.getCourtId());

            String date = event.getStartTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String startTime = event.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            String endTime = event.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

            // İptal emaili gönder
            boolean emailSent = emailService.sendCancellationEmail(
                event.getReservationId(),
                userProfile.getEmail(),
                court.getName(),
                startTime,
                endTime
            );
            
            if (emailSent) {
                logger.info("Cancellation email sent for reservation: {}", event.getReservationId());
                
                // SMS gönder
                smsService.sendCancellationSms(
                    userProfile.getPhoneNumber(),
                    court.getName(),
                    date,
                    startTime,
                    event.getReservationId()
                );
            }

            // Schedule'lanmış hatırlatmaları iptal et
            schedulerService.cancelScheduledReminders(event.getReservationId());

        } catch (Exception e) {
            logger.error("Error handling reservation-cancelled event for reservation: {}", event.getReservationId(), e);
        }
    }

    // kullanıcı kayıt event'ini dinler
    @KafkaListener(topics = "user-registered", groupId = "notification-service")
    public void handleUserRegistered(UserRegisteredEvent event) {
        logger.info("Received user-registered event for user: {}", event.getUserId());

        try {
            // Hoş geldin emaili gönder
            boolean emailSent = emailService.sendWelcomeEmail(
                event.getEmail(),
                event.getFirstName(),
                event.getLastName()
            );
            
            if (emailSent) {
                logger.info("Welcome email sent to user: {}", event.getUserId());
            }

            // Varsayılan notification preferences oluştur
            preferencesService.createDefaultPreferences(event.getUserId());
            logger.info("Default preferences created for user: {}", event.getUserId());

        } catch (Exception e) {
            logger.error("Error handling user-registered event for user: {}", event.getUserId(), e);
        }
    }

    // iade tamamlandı event'ini dinler
    @KafkaListener(topics = "refund-completed", groupId = "notification-service")
    public void handleRefundCompleted(PaymentCompletedEvent event) {
        logger.info("Received refund-completed event for payment: {}", event.getPaymentId());

        try {
            UserProfileResponse userProfile = userServiceClient.getUserProfile(event.getUserId());

            // İade bildirimi emaili gönder
            boolean emailSent = emailService.sendRefundNotificationEmail(
                userProfile.getEmail(),
                event.getAmount().toString(),
                event.getCurrency(),
                event.getReservationId()
            );
            
            if (emailSent) {
                logger.info("Refund notification email sent for payment: {}", event.getPaymentId());
            }

        } catch (Exception e) {
            logger.error("Error handling refund-completed event for payment: {}", event.getPaymentId(), e);
        }
    }

    // ödeme başarısız event'ini dinler
    @KafkaListener(topics = "payment-failed", groupId = "notification-service")
    public void handlePaymentFailed(PaymentCompletedEvent event) {
        logger.info("Received payment-failed event for payment: {}", event.getPaymentId());

        try {
            UserProfileResponse userProfile = userServiceClient.getUserProfile(event.getUserId());

            // Ödeme başarısız bildirimi (basit email)
            boolean emailSent = emailService.sendEmail(
                userProfile.getEmail(),
                "Ödeme Başarısız - SportHub",
                "Rezervasyon No: " + event.getReservationId() + " için ödeme işlemi başarısız oldu. Lütfen tekrar deneyiniz.",
                null
            );
            
            if (emailSent) {
                logger.info("Payment failed notification sent for payment: {}", event.getPaymentId());
            }

        } catch (Exception e) {
            logger.error("Error handling payment-failed event for payment: {}", event.getPaymentId(), e);
        }
    }
}
