package com.sporthub.rezervation_service.repository;

import com.sporthub.rezervation_service.model.ReservationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationHistoryRepository extends JpaRepository<ReservationHistory, Long> {
    List<ReservationHistory> findByReservationIdOrderByActionTimestampDesc(Long reservationId);
}


