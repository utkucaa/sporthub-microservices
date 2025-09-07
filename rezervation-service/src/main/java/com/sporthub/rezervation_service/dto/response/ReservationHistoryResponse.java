package com.sporthub.rezervation_service.dto.response;

import java.time.LocalDateTime;

public class ReservationHistoryResponse {
    private Long reservationId;
    private String action;
    private Long performedBy;
    private LocalDateTime actionTimestamp;
    private String details;

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Long getPerformedBy() { return performedBy; }
    public void setPerformedBy(Long performedBy) { this.performedBy = performedBy; }
    public LocalDateTime getActionTimestamp() { return actionTimestamp; }
    public void setActionTimestamp(LocalDateTime actionTimestamp) { this.actionTimestamp = actionTimestamp; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}


