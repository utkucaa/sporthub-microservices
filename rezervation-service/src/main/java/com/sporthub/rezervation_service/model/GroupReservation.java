package com.sporthub.rezervation_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_reservations",
        uniqueConstraints = @UniqueConstraint(name = "unique_participant", columnNames = {"reservation_id", "participant_user_id"}))
public class GroupReservation {

    public enum GroupStatus { PENDING, ACCEPTED, DECLINED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Column(name = "participant_user_id", nullable = false)
    private Long participantUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GroupStatus status = GroupStatus.PENDING;

    @Column(name = "invited_at", nullable = false)
    private LocalDateTime invitedAt = LocalDateTime.now();

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public Long getParticipantUserId() { return participantUserId; }
    public void setParticipantUserId(Long participantUserId) { this.participantUserId = participantUserId; }
    public GroupStatus getStatus() { return status; }
    public void setStatus(GroupStatus status) { this.status = status; }
    public LocalDateTime getInvitedAt() { return invitedAt; }
    public void setInvitedAt(LocalDateTime invitedAt) { this.invitedAt = invitedAt; }
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
}


