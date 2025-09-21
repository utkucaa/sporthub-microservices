package com.sporthub.notification.controller;

import com.sporthub.notification.dto.request.BulkNotificationRequest;
import com.sporthub.notification.dto.request.SendNotificationRequest;
import com.sporthub.notification.dto.response.NotificationResponse;
import com.sporthub.notification.enums.NotificationStatus;
import com.sporthub.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    // manuel bildirim gönder
    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@Valid @RequestBody SendNotificationRequest request) {
        logger.info("Sending notification to user: {}", request.getUserId());

        try {
            NotificationResponse response = notificationService.sendNotification(request);
            
            if (response != null) {
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Notification not allowed for user preferences");
                return ResponseEntity.badRequest().body(errorResponse);
            }

        } catch (Exception e) {
            logger.error("Error sending notification", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to send notification");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // toplu bildirim gönder
    @PostMapping("/send/bulk")
    public ResponseEntity<?> sendBulkNotification(@Valid @RequestBody BulkNotificationRequest request) {
        logger.info("Sending bulk notification to {} users", request.getUserIds().size());

        try {
            notificationService.sendBulkNotification(request);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Bulk notifications sent successfully");
            response.put("userCount", String.valueOf(request.getUserIds().size()));
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error sending bulk notification", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to send bulk notification");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // kullanıcı bildirimlerini getir
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationResponse>> getUserNotifications(
            @PathVariable Long userId, Pageable pageable) {
        
        logger.info("Getting notifications for user: {}", userId);

        try {
            Page<NotificationResponse> notifications = notificationService.getNotificationHistory(userId, pageable);
            return ResponseEntity.ok(notifications);

        } catch (Exception e) {
            logger.error("Error getting user notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotification(@PathVariable Long id) {
        logger.info("Getting notification: {}", id);

        try {
            // TODO: detay metodu eklenecek
            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification detail endpoint - to be implemented");
            return ResponseEntity.ok(null);

        } catch (Exception e) {
            logger.error("Error getting notification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // okundu işaretle
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        logger.info("Marking notification as read: {}", id);

        try {
            notificationService.updateNotificationStatus(id, NotificationStatus.READ);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Notification marked as read");
            response.put("notificationId", String.valueOf(id));
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error marking notification as read", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to mark notification as read");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // okunmamış sayısı
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<?> getUnreadCount(@PathVariable Long userId) {
        logger.info("Getting unread count for user: {}", userId);

        try {
            Long unreadCount = notificationService.getUnreadCount(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("unreadCount", unreadCount);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting unread count", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to get unread count");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // başarısız bildirimleri tekrar dene
    @PostMapping("/retry-failed")
    public ResponseEntity<?> retryFailedNotifications() {
        logger.info("Retrying failed notifications");

        try {
            notificationService.retryFailedNotifications();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed notifications retry initiated");
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error retrying failed notifications", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retry notifications");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
