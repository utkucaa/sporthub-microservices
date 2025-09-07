package com.sporthub.rezervation_service.service;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class TimeSlotValidator {
    private static final int MIN_MINUTES = 30;
    private static final int MAX_HOURS = 4;
    private static final int MAX_DAYS_AHEAD = 30;
    private static final int BUSINESS_OPEN_HOUR = 6;
    private static final int BUSINESS_CLOSE_HOUR = 24;

    public void validateBusinessRules(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new IllegalArgumentException("Invalid time range");
        }
        Duration duration = Duration.between(start, end);
        if (duration.toMinutes() < MIN_MINUTES) {
            throw new IllegalArgumentException("Minimum booking is 30 minutes");
        }
        if (duration.toHours() > MAX_HOURS) {
            throw new IllegalArgumentException("Maximum booking is 4 hours");
        }
        if (Duration.between(LocalDateTime.now(), start).toDays() > MAX_DAYS_AHEAD) {
            throw new IllegalArgumentException("Advance booking limit is 30 days");
        }
        int startHour = start.getHour();
        int endHour = end.getHour();
        if (startHour < BUSINESS_OPEN_HOUR || endHour > BUSINESS_CLOSE_HOUR) {
            throw new IllegalArgumentException("Outside business hours 06:00-24:00");
        }
    }
}


