package com.sporthub.rezervation_service.service;

import com.sporthub.rezervation_service.dto.response.ReservationHistoryResponse;
import com.sporthub.rezervation_service.model.Reservation;
import com.sporthub.rezervation_service.model.ReservationHistory;
import com.sporthub.rezervation_service.repository.ReservationHistoryRepository;
import com.sporthub.rezervation_service.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationHistoryService {
    private final ReservationRepository reservationRepository;
    private final ReservationHistoryRepository historyRepository;

    public ReservationHistoryService(ReservationRepository reservationRepository,
                                     ReservationHistoryRepository historyRepository) {
        this.reservationRepository = reservationRepository;
        this.historyRepository = historyRepository;
    }

    public List<ReservationHistoryResponse> getHistoryByUser(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserIdOrderByStartTimeDesc(userId);
        List<ReservationHistoryResponse> responses = new ArrayList<>();
        for (Reservation r : reservations) {
            var items = historyRepository.findByReservationIdOrderByActionTimestampDesc(r.getId());
            for (ReservationHistory h : items) {
                ReservationHistoryResponse dto = new ReservationHistoryResponse();
                dto.setReservationId(r.getId());
                dto.setAction(h.getAction().name());
                dto.setPerformedBy(h.getPerformedBy());
                dto.setActionTimestamp(h.getActionTimestamp());
                dto.setDetails(h.getDetails());
                responses.add(dto);
            }
        }
        return responses;
    }
}


