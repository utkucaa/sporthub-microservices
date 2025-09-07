package com.sporthub.rezervation_service.service;

import com.sporthub.rezervation_service.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConflictResolutionService {
    private final ReservationRepository reservationRepository;

    public ConflictResolutionService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public boolean hasConflict(Long courtId, LocalDateTime start, LocalDateTime end, Long excludeReservationId) {
        return reservationRepository.findConflicts(courtId, start, end).stream()
                .anyMatch(r -> excludeReservationId == null || !r.getId().equals(excludeReservationId));
    }
}


