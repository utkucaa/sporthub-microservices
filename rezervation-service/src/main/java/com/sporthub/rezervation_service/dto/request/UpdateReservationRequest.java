package com.sporthub.rezervation_service.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UpdateReservationRequest {
    @NotNull
    private Long id;
    @Future
    private LocalDateTime startTime;
    @Future
    private LocalDateTime endTime;
    private BigDecimal totalPrice;
    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}


