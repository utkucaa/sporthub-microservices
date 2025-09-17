package com.sporthub.payment_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public class WebhookRequest {

    @NotBlank(message = "Payload is required")
    private String payload;

    @NotBlank(message = "Signature is required")
    private String signature;

    public WebhookRequest() {}

    public WebhookRequest(String payload, String signature) {
        this.payload = payload;
        this.signature = signature;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "WebhookRequest{" +
                "payloadLength=" + (payload != null ? payload.length() : 0) +
                ", hasSignature=" + (signature != null && !signature.isEmpty()) +
                '}';
    }
}
