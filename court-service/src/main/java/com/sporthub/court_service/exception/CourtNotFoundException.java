package com.sporthub.court_service.exception;

public class CourtNotFoundException extends RuntimeException {
    
    public CourtNotFoundException(String message) {
        super(message);
    }
    
    public CourtNotFoundException(Long id) {
        super("Court not found with id: " + id);
    }
}
