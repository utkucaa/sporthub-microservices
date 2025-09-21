package com.sporthub.notification.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Completed Event DTO
 */
public class PaymentCompletedEvent {

    private Long paymentId;
    private Long reservationId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String status;
    private LocalDateTime completedAt;

    // Default constructor
    public PaymentCompletedEvent() {}

    // Constructor
    public PaymentCompletedEvent(Long paymentId, Long reservationId, Long userId, BigDecimal amount,
                                String currency, String paymentMethod, String status, LocalDateTime completedAt) {
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.completedAt = completedAt;
    }

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
