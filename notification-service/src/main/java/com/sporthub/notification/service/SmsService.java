package com.sporthub.notification.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Value("${notification.sms.account-sid}")
    private String accountSid;

    @Value("${notification.sms.auth-token}")
    private String authToken;

    @Value("${notification.sms.from-number}")
    private String fromNumber;

    @PostConstruct
    public void initTwilio() {
        if (accountSid != null && !accountSid.startsWith("${")) {
            Twilio.init(accountSid, authToken);
            logger.info("Twilio initialized successfully");
        } else {
            logger.warn("Twilio credentials not configured, SMS service will be disabled");
        }
    }

    // temel sms gönderme
    public boolean sendSms(String phoneNumber, String message) {
        if (accountSid == null || accountSid.startsWith("${")) {
            logger.warn("SMS service not configured, skipping SMS to: {}", phoneNumber);
            return false;
        }

        try {
            Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(fromNumber),
                message
            ).create();

            logger.info("SMS sent successfully to: {}", phoneNumber);
            return true;

        } catch (Exception e) {
            logger.error("Failed to send SMS to: {}", phoneNumber, e);
            return false;
        }
    }

    // template ile sms gönder
    public boolean sendTemplateSms(String templateName, String phoneNumber, Map<String, Object> variables) {
        try {
            String message = processTemplate(templateName, variables);
            return sendSms(phoneNumber, message);

        } catch (Exception e) {
            logger.error("Failed to send template SMS: {}, to: {}", templateName, phoneNumber, e);
            return false;
        }
    }

    // rezervasyon hatırlatma sms
    public boolean sendReservationReminderSms(String phoneNumber, String courtName, String startTime, String endTime) {
        String message = String.format(
            "SportHub Hatırlatma: %s sahasındaki rezervasyonunuz %s-%s saatleri arasında başlayacak. İyi oyunlar!",
            courtName, startTime, endTime
        );

        return sendSms(phoneNumber, message);
    }

    // rezervasyon onay sms
    public boolean sendReservationConfirmationSms(String phoneNumber, String courtName, String date, 
                                                 String startTime, String endTime, Long reservationId) {
        String message = String.format(
            "SportHub: Rezervasyonunuz onaylandı! %s - %s %s-%s, Rezervasyon No: %d. İyi oyunlar!",
            courtName, date, startTime, endTime, reservationId
        );

        return sendSms(phoneNumber, message);
    }

    // ödeme onay sms
    public boolean sendPaymentConfirmationSms(String phoneNumber, String amount, Long reservationId) {
        String message = String.format(
            "SportHub: %s TL ödemeniz alındı. Rezervasyon No: %d. Teşekkürler!",
            amount, reservationId
        );

        return sendSms(phoneNumber, message);
    }

    // iptal sms
    public boolean sendCancellationSms(String phoneNumber, String courtName, String date, 
                                     String startTime, Long reservationId) {
        String message = String.format(
            "SportHub: Rezervasyonunuz iptal edildi. %s - %s %s, Rezervasyon No: %d",
            courtName, date, startTime, reservationId
        );

        return sendSms(phoneNumber, message);
    }

    private String processTemplate(String templateName, Map<String, Object> variables) {
        // basit template işleme
        switch (templateName) {
            case "reservation-reminder":
                return String.format(
                    "SportHub: %s sahasındaki rezervasyonunuz yaklaşıyor. %s-%s",
                    variables.get("courtName"),
                    variables.get("startTime"),
                    variables.get("endTime")
                );
            default:
                return "SportHub bildirimi";
        }
    }
}
