package com.sporthub.notification.enums;

/**
 * Hatırlatma türlerini tanımlayan enum
 */
public enum ReminderType {
    REMINDER_24H(24 * 60), // 24 saat = 1440 dakika
    REMINDER_1H(60),       // 1 saat = 60 dakika
    REMINDER_30M(30);      // 30 dakika

    private final int minutesBefore;

    ReminderType(int minutesBefore) {
        this.minutesBefore = minutesBefore;
    }

    public int getMinutesBefore() {
        return minutesBefore;
    }
}
