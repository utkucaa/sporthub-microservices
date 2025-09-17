package com.sporthub.payment_service.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservationEvent {

    private Long reservationId;
    private Long userId;
    private Long courtId;
    private BigDecimal totalPrice;
    private String currency;
    private String status;
    private String paymentStatus;
    private String startTime;
    private String endTime;
    private LocalDateTime timestamp;

    public ReservationEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public ReservationEvent(Long reservationId, Long userId, Long courtId, 
                           BigDecimal totalPrice, String currency, String status) {
        this();
        this.reservationId = reservationId;
        this.userId = userId;
        this.courtId = courtId;
        this.totalPrice = totalPrice;
        this.currency = currency;
        this.status = status;
    }

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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ReservationEvent{" +
                "reservationId=" + reservationId +
                ", userId=" + userId +
                ", courtId=" + courtId +
                ", totalPrice=" + totalPrice +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
