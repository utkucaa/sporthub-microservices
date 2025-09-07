package com.sporthub.rezervation_service.repository;

import com.sporthub.rezervation_service.model.Reservation;
import com.sporthub.rezervation_service.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserIdOrderByStartTimeDesc(Long userId);

    @Query("select r from Reservation r where r.courtId = :courtId and r.status in (com.sporthub.rezervation_service.model.ReservationStatus.PENDING, com.sporthub.rezervation_service.model.ReservationStatus.CONFIRMED) and ((r.startTime < :endTime and r.endTime > :startTime))")
    List<Reservation> findConflicts(@Param("courtId") Long courtId,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);

    List<Reservation> findByCourtIdAndStartTimeBetween(Long courtId, LocalDateTime start, LocalDateTime end);

    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);
}


