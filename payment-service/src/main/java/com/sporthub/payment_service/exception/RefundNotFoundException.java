package com.sporthub.payment_service.exception;

public class RefundNotFoundException extends RuntimeException {

    public RefundNotFoundException(String message) {
        super(message);
    }

    public RefundNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
