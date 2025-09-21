package com.sporthub.notification.dto.request;

import com.sporthub.notification.enums.ReminderType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Schedule Reminder Request DTO
 */
public class ScheduleReminderRequest {

    @NotNull(message = "Reservation ID is required")
    private Long reservationId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Reminder types are required")
    private List<ReminderType> reminderTypes;

    // Default constructor
    public ScheduleReminderRequest() {}

    // Constructor
    public ScheduleReminderRequest(Long reservationId, Long userId, List<ReminderType> reminderTypes) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.reminderTypes = reminderTypes;
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

    public List<ReminderType> getReminderTypes() {
        return reminderTypes;
    }

    public void setReminderTypes(List<ReminderType> reminderTypes) {
        this.reminderTypes = reminderTypes;
    }
}
