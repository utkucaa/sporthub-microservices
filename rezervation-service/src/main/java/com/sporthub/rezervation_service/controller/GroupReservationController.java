package com.sporthub.rezervation_service.controller;

import com.sporthub.rezervation_service.model.GroupReservation;
import com.sporthub.rezervation_service.service.GroupReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class GroupReservationController {
    private final GroupReservationService groupReservationService;

    public GroupReservationController(GroupReservationService groupReservationService) {
        this.groupReservationService = groupReservationService;
    }

    @PostMapping("/{id}/group/invite")
    public ResponseEntity<GroupReservation> invite(@PathVariable("id") Long reservationId,
                                                   @RequestParam("participantUserId") Long participantUserId) {
        return ResponseEntity.ok(groupReservationService.invite(reservationId, participantUserId));
    }

    @PostMapping("/group/respond/{invitationId}")
    public ResponseEntity<GroupReservation> respond(@PathVariable Long invitationId,
                                                    @RequestParam boolean accept) {
        return ResponseEntity.ok(groupReservationService.respond(invitationId, accept));
    }

    @GetMapping("/{id}/group")
    public ResponseEntity<List<GroupReservation>> list(@PathVariable("id") Long reservationId) {
        return ResponseEntity.ok(groupReservationService.list(reservationId));
    }
}


