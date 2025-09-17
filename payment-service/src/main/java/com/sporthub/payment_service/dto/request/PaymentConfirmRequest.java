package com.sporthub.payment_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public class PaymentConfirmRequest {

    @NotBlank(message = "Payment intent ID is required")
    private String paymentIntentId;

    @NotBlank(message = "Payment method ID is required")
    private String paymentMethodId;

    public PaymentConfirmRequest() {}

    public PaymentConfirmRequest(String paymentIntentId, String paymentMethodId) {
        this.paymentIntentId = paymentIntentId;
        this.paymentMethodId = paymentMethodId;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    @Override
    public String toString() {
        return "PaymentConfirmRequest{" +
                "paymentIntentId='" + paymentIntentId + '\'' +
                ", paymentMethodId='" + paymentMethodId + '\'' +
                '}';
    }
}
