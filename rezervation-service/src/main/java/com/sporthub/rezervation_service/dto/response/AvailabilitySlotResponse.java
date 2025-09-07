package com.sporthub.rezervation_service.dto.response;

import java.time.LocalDateTime;

public class AvailabilitySlotResponse {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean available;

    public AvailabilitySlotResponse() {}
    public AvailabilitySlotResponse(LocalDateTime startTime, LocalDateTime endTime, boolean available) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}


