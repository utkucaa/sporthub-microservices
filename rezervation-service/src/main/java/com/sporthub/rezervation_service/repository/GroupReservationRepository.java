package com.sporthub.rezervation_service.repository;

import com.sporthub.rezervation_service.model.GroupReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupReservationRepository extends JpaRepository<GroupReservation, Long> {
    List<GroupReservation> findByReservationId(Long reservationId);
    List<GroupReservation> findByParticipantUserId(Long participantUserId);
}


