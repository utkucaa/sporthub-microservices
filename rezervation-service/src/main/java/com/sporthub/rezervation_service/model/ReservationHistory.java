package com.sporthub.rezervation_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_history")
public class ReservationHistory {

    public enum Action { CREATED, CONFIRMED, CANCELLED, UPDATED, COMPLETED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private Action action;

    @Column(name = "performed_by")
    private Long performedBy;

    @Column(name = "action_timestamp", nullable = false)
    private LocalDateTime actionTimestamp = LocalDateTime.now();

    @Lob
    @Column(name = "details")
    private String details;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }
    public Long getPerformedBy() { return performedBy; }
    public void setPerformedBy(Long performedBy) { this.performedBy = performedBy; }
    public LocalDateTime getActionTimestamp() { return actionTimestamp; }
    public void setActionTimestamp(LocalDateTime actionTimestamp) { this.actionTimestamp = actionTimestamp; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}


