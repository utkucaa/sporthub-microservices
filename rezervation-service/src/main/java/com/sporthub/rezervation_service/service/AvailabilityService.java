package com.sporthub.rezervation_service.service;

import com.sporthub.rezervation_service.dto.response.AvailabilitySlotResponse;
import com.sporthub.rezervation_service.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// saha müsaitlik durumunu kontrol eden servis
@Service
public class AvailabilityService {
    private final ReservationRepository reservationRepository;

    public AvailabilityService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // belirli gün için saha müsaitliği
    public List<AvailabilitySlotResponse> getCourtAvailability(Long courtId, LocalDate date) {
        LocalDateTime startOfDay = date.atTime(LocalTime.of(6, 0));
        LocalDateTime endOfDay = date.atTime(LocalTime.of(23, 59));
        var reservations = reservationRepository.findByCourtIdAndStartTimeBetween(courtId, startOfDay, endOfDay);
        List<AvailabilitySlotResponse> responses = new ArrayList<>();
        reservations.forEach(r -> responses.add(new AvailabilitySlotResponse(r.getStartTime(), r.getEndTime(), false)));
        return responses;
    }

    public List<AvailabilitySlotResponse> bulkCheck(Long courtId, List<LocalDateTime[]> ranges) {
        List<AvailabilitySlotResponse> results = new ArrayList<>();
        for (LocalDateTime[] range : ranges) {
            boolean conflict = !reservationRepository.findConflicts(courtId, range[0], range[1]).isEmpty();
            results.add(new AvailabilitySlotResponse(range[0], range[1], !conflict));
        }
        return results;
    }
}


