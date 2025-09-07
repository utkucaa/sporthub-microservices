package com.sporthub.rezervation_service.exception;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(Long id) {
        super("Reservation not found: " + id);
    }
}


