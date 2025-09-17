package com.sporthub.payment_service.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RefundEvent {

    private Long refundId;
    private Long paymentId;
    private Long reservationId;
    private Long userId;
    private String stripeRefundId;
    private BigDecimal amount;
    private String reason;
    private String status;
    private String initiatedBy;
    private LocalDateTime timestamp;

    public RefundEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public RefundEvent(Long refundId, Long paymentId, Long reservationId, Long userId,
                      BigDecimal amount, String reason, String status) {
        this();
        this.refundId = refundId;
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.userId = userId;
        this.amount = amount;
        this.reason = reason;
        this.status = status;
    }

    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

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

    public String getStripeRefundId() {
        return stripeRefundId;
    }

    public void setStripeRefundId(String stripeRefundId) {
        this.stripeRefundId = stripeRefundId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "RefundEvent{" +
                "refundId=" + refundId +
                ", paymentId=" + paymentId +
                ", reservationId=" + reservationId +
                ", userId=" + userId +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
