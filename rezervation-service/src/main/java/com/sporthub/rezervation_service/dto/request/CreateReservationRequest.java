package com.sporthub.rezervation_service.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateReservationRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long courtId;
    @NotNull @Future
    private LocalDateTime startTime;
    @NotNull @Future
    private LocalDateTime endTime;
    @Min(0)
    private BigDecimal totalPrice;
    private String notes;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCourtId() { return courtId; }
    public void setCourtId(Long courtId) { this.courtId = courtId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}


