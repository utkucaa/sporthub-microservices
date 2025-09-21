package com.sporthub.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PushNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(PushNotificationService.class);

    // temel push notification gönderme
    public boolean sendPushNotification(String deviceToken, String title, String body, Map<String, Object> data) {
        if (deviceToken == null || deviceToken.isEmpty()) {
            logger.warn("Device token is empty, skipping push notification");
            return false;
        }

        try {
            Message.Builder messageBuilder = Message.builder()
                .setToken(deviceToken)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build());

            if (data != null && !data.isEmpty()) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    messageBuilder.putData(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }

            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            logger.info("Push notification sent successfully: {}", response);
            return true;

        } catch (Exception e) {
            logger.error("Failed to send push notification to token: {}", deviceToken, e);
            return false;
        }
    }

    // toplu push notification
    public boolean sendBulkPushNotification(List<String> deviceTokens, String title, String body) {
        if (deviceTokens == null || deviceTokens.isEmpty()) {
            logger.warn("Device tokens list is empty, skipping bulk push notification");
            return false;
        }

        boolean allSent = true;
        for (String token : deviceTokens) {
            boolean sent = sendPushNotification(token, title, body, null);
            if (!sent) {
                allSent = false;
            }
        }

        return allSent;
    }

    // rezervasyon hatırlatma push
    public boolean sendReservationReminderPush(String deviceToken, String courtName, String startTime, String endTime) {
        String title = "Rezervasyon Hatırlatması";
        String body = String.format("%s sahasındaki rezervasyonunuz %s-%s saatleri arasında başlayacak",
                                   courtName, startTime, endTime);

        return sendPushNotification(deviceToken, title, body, null);
    }

    // rezervasyon onay push
    public boolean sendReservationConfirmationPush(String deviceToken, String courtName, String date, 
                                                   String startTime, String endTime, Long reservationId) {
        String title = "Rezervasyon Onayı";
        String body = String.format("Rezervasyonunuz onaylandı! %s - %s %s-%s",
                                   courtName, date, startTime, endTime);

        Map<String, Object> data = Map.of(
            "type", "reservation_confirmation",
            "reservationId", reservationId.toString(),
            "courtName", courtName
        );

        return sendPushNotification(deviceToken, title, body, data);
    }

    // ödeme onay push
    public boolean sendPaymentConfirmationPush(String deviceToken, String amount, Long reservationId) {
        String title = "Ödeme Onayı";
        String body = String.format("%s TL ödemeniz başarıyla alındı", amount);

        Map<String, Object> data = Map.of(
            "type", "payment_confirmation",
            "reservationId", reservationId.toString(),
            "amount", amount
        );

        return sendPushNotification(deviceToken, title, body, data);
    }

    // iptal push
    public boolean sendCancellationPush(String deviceToken, String courtName, String date, 
                                       String startTime, Long reservationId) {
        String title = "Rezervasyon İptali";
        String body = String.format("Rezervasyonunuz iptal edildi: %s - %s %s",
                                   courtName, date, startTime);

        Map<String, Object> data = Map.of(
            "type", "reservation_cancellation",
            "reservationId", reservationId.toString(),
            "courtName", courtName
        );

        return sendPushNotification(deviceToken, title, body, data);
    }

    // hoş geldin push
    public boolean sendWelcomePush(String deviceToken, String firstName) {
        String title = "SportHub'a Hoş Geldiniz!";
        String body = String.format("Merhaba %s, SportHub ailesine katıldığınız için teşekkürler!", firstName);

        Map<String, Object> data = Map.of(
            "type", "welcome",
            "firstName", firstName
        );

        return sendPushNotification(deviceToken, title, body, data);
    }
}
