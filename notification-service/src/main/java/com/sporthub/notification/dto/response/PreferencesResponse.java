package com.sporthub.notification.dto.response;

/**
 * Preferences Response DTO
 */
public class PreferencesResponse {

    private Long id;
    private Long userId;
    private Boolean emailEnabled;
    private Boolean smsEnabled;
    private Boolean pushEnabled;
    private Boolean marketingEnabled;
    private Boolean reservationReminders;
    private Boolean paymentNotifications;
    private Integer reminderHours;

    // Default constructor
    public PreferencesResponse() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(Boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public Boolean getSmsEnabled() {
        return smsEnabled;
    }

    public void setSmsEnabled(Boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public Boolean getPushEnabled() {
        return pushEnabled;
    }

    public void setPushEnabled(Boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public Boolean getMarketingEnabled() {
        return marketingEnabled;
    }

    public void setMarketingEnabled(Boolean marketingEnabled) {
        this.marketingEnabled = marketingEnabled;
    }

    public Boolean getReservationReminders() {
        return reservationReminders;
    }

    public void setReservationReminders(Boolean reservationReminders) {
        this.reservationReminders = reservationReminders;
    }

    public Boolean getPaymentNotifications() {
        return paymentNotifications;
    }

    public void setPaymentNotifications(Boolean paymentNotifications) {
        this.paymentNotifications = paymentNotifications;
    }

    public Integer getReminderHours() {
        return reminderHours;
    }

    public void setReminderHours(Integer reminderHours) {
        this.reminderHours = reminderHours;
    }
}
