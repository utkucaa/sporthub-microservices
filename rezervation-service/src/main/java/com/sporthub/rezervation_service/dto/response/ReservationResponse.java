package com.sporthub.rezervation_service.dto.response;

import com.sporthub.rezervation_service.model.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservationResponse {
    private Long id;
    private Long userId;
    private Long courtId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalPrice;
    private ReservationStatus status;
    private String paymentId;
    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}


