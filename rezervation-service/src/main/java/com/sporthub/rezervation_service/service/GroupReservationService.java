package com.sporthub.rezervation_service.service;

import com.sporthub.rezervation_service.model.GroupReservation;
import com.sporthub.rezervation_service.repository.GroupReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GroupReservationService {
    private final GroupReservationRepository groupReservationRepository;

    public GroupReservationService(GroupReservationRepository groupReservationRepository) {
        this.groupReservationRepository = groupReservationRepository;
    }

    public GroupReservation invite(Long reservationId, Long participantUserId) {
        GroupReservation gr = new GroupReservation();
        gr.setReservationId(reservationId);
        gr.setParticipantUserId(participantUserId);
        gr.setInvitedAt(LocalDateTime.now());
        return groupReservationRepository.save(gr);
    }

    public GroupReservation respond(Long invitationId, boolean accept) {
        GroupReservation gr = groupReservationRepository.findById(invitationId).orElseThrow();
        gr.setStatus(accept ? GroupReservation.GroupStatus.ACCEPTED : GroupReservation.GroupStatus.DECLINED);
        gr.setRespondedAt(LocalDateTime.now());
        return groupReservationRepository.save(gr);
    }

    public List<GroupReservation> list(Long reservationId) {
        return groupReservationRepository.findByReservationId(reservationId);
    }
}


