package com.sporthub.notification.dto.event;

import java.time.LocalDateTime;

/**
 * Notification Sent Event DTO
 */
public class NotificationSentEvent {

    private Long reservationId;
    private Long userId;
    private String notificationType;
    private LocalDateTime sentAt;

    // Default constructor
    public NotificationSentEvent() {}

    // Constructor
    public NotificationSentEvent(Long reservationId, Long userId, String notificationType, LocalDateTime sentAt) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.notificationType = notificationType;
        this.sentAt = sentAt;
    }

    // Getters and Setters
    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
