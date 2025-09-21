package com.sporthub.notification.service;

import com.sporthub.notification.dto.event.PaymentCompletedEvent;
import com.sporthub.notification.dto.event.ReservationCreatedEvent;
import com.sporthub.notification.entity.NotificationSchedule;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private NotificationTemplateService templateService;

    @Value("${notification.email.from}")
    private String fromEmail;

    @Value("${notification.email.from-name}")
    private String fromName;

    // temel email gönderme metodu
    public boolean sendEmail(String to, String subject, String content, Map<String, Object> variables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
            return true;

        } catch (Exception e) {
            logger.error("Failed to send email to: {}", to, e);
            return false;
        }
    }

    // template kullanarak email gönder
    public boolean sendTemplateEmail(String templateName, String to, Map<String, Object> variables) {
        try {
            String processedContent = templateService.processTemplate(templateName, variables);
            String subject = (String) variables.getOrDefault("subject", "SportHub Bildirimi");

            return sendEmail(to, subject, processedContent, variables);

        } catch (Exception e) {
            logger.error("Failed to send template email: {}, to: {}", templateName, to, e);
            return false;
        }
    }

    // rezervasyon onay emaili
    public boolean sendReservationConfirmationEmail(ReservationCreatedEvent event, String userEmail) {
        try {
            Context context = new Context();
            context.setVariable("firstName", "Değerli Müşterimiz"); // User service'den alınacak
            context.setVariable("courtName", event.getCourtName());
            context.setVariable("date", event.getStartTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            context.setVariable("startTime", event.getStartTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            context.setVariable("endTime", event.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            context.setVariable("amount", event.getAmount());
            context.setVariable("reservationId", event.getReservationId());

            String content = templateEngine.process("reservation-confirmation", context);
            String subject = "Rezervasyon Onayı - SportHub";

            return sendEmail(userEmail, subject, content, null);

        } catch (Exception e) {
            logger.error("Failed to send reservation confirmation email for reservation: {}", event.getReservationId(), e);
            return false;
        }
    }

    // ödeme onay emaili
    public boolean sendPaymentConfirmationEmail(PaymentCompletedEvent event, String userEmail) {
        try {
            Context context = new Context();
            context.setVariable("firstName", "Değerli Müşterimiz");
            context.setVariable("amount", event.getAmount());
            context.setVariable("currency", event.getCurrency());
            context.setVariable("paymentMethod", event.getPaymentMethod());
            context.setVariable("paymentId", event.getPaymentId());
            context.setVariable("reservationId", event.getReservationId());
            context.setVariable("completedAt", event.getCompletedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

            String content = templateEngine.process("payment-confirmation", context);
            String subject = "Ödeme Onayı - SportHub";

            return sendEmail(userEmail, subject, content, null);

        } catch (Exception e) {
            logger.error("Failed to send payment confirmation email for payment: {}", event.getPaymentId(), e);
            return false;
        }
    }

    // hatırlatma emaili
    public boolean sendReminderEmail(NotificationSchedule schedule, String userEmail, String courtName, 
                                   String startTime, String endTime) {
        try {
            Context context = new Context();
            context.setVariable("firstName", "Değerli Müşterimiz");
            context.setVariable("courtName", courtName);
            context.setVariable("startTime", startTime);
            context.setVariable("endTime", endTime);
            context.setVariable("reservationId", schedule.getReservationId());
            context.setVariable("reminderType", schedule.getReminderType().name());

            String content = templateEngine.process("reservation-reminder", context);
            String subject = "Rezervasyon Hatırlatması - SportHub";

            return sendEmail(userEmail, subject, content, null);

        } catch (Exception e) {
            logger.error("Failed to send reminder email for schedule: {}", schedule.getId(), e);
            return false;
        }
    }

    // iptal emaili
    public boolean sendCancellationEmail(Long reservationId, String userEmail, String courtName, 
                                       String startTime, String endTime) {
        try {
            Context context = new Context();
            context.setVariable("firstName", "Değerli Müşterimiz");
            context.setVariable("courtName", courtName);
            context.setVariable("startTime", startTime);
            context.setVariable("endTime", endTime);
            context.setVariable("reservationId", reservationId);

            String content = templateEngine.process("reservation-cancellation", context);
            String subject = "Rezervasyon İptali - SportHub";

            return sendEmail(userEmail, subject, content, null);

        } catch (Exception e) {
            logger.error("Failed to send cancellation email for reservation: {}", reservationId, e);
            return false;
        }
    }

    // hoş geldin emaili
    public boolean sendWelcomeEmail(String userEmail, String firstName, String lastName) {
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("lastName", lastName);

            String content = templateEngine.process("welcome-email", context);
            String subject = "SportHub'a Hoş Geldiniz!";

            return sendEmail(userEmail, subject, content, null);

        } catch (Exception e) {
            logger.error("Failed to send welcome email to: {}", userEmail, e);
            return false;
        }
    }

    // iade bildirimi emaili
    public boolean sendRefundNotificationEmail(String userEmail, String amount, String currency, 
                                             Long reservationId) {
        try {
            Context context = new Context();
            context.setVariable("firstName", "Değerli Müşterimiz");
            context.setVariable("amount", amount);
            context.setVariable("currency", currency);
            context.setVariable("reservationId", reservationId);

            String content = templateEngine.process("refund-notification", context);
            String subject = "İade İşlemi Tamamlandı - SportHub";

            return sendEmail(userEmail, subject, content, null);

        } catch (Exception e) {
            logger.error("Failed to send refund notification email for reservation: {}", reservationId, e);
            return false;
        }
    }
}
