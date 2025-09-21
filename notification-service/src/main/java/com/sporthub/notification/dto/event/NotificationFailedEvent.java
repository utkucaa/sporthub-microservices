package com.sporthub.notification.dto.event;

import java.time.LocalDateTime;

/**
 * Notification Failed Event DTO
 */
public class NotificationFailedEvent {

    private Long reservationId;
    private Long userId;
    private String notificationType;
    private String failureReason;
    private LocalDateTime failedAt;

    // Default constructor
    public NotificationFailedEvent() {}

    // Constructor
    public NotificationFailedEvent(Long reservationId, Long userId, String notificationType, 
                                  String failureReason, LocalDateTime failedAt) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.notificationType = notificationType;
        this.failureReason = failureReason;
        this.failedAt = failedAt;
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

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }
}
