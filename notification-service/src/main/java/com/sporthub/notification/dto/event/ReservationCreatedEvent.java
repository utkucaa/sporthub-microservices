package com.sporthub.notification.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Reservation Created Event DTO
 */
public class ReservationCreatedEvent {

    private Long reservationId;
    private Long userId;
    private Long courtId;
    private String courtName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal amount;
    private String status;

    // Default constructor
    public ReservationCreatedEvent() {}

    // Constructor
    public ReservationCreatedEvent(Long reservationId, Long userId, Long courtId, String courtName,
                                  LocalDateTime startTime, LocalDateTime endTime, BigDecimal amount, String status) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.courtId = courtId;
        this.courtName = courtName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amount = amount;
        this.status = status;
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

    public Long getCourtId() {
        return courtId;
    }

    public void setCourtId(Long courtId) {
        this.courtId = courtId;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
