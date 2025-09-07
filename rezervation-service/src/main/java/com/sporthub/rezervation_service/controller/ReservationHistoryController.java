package com.sporthub.rezervation_service.controller;

import com.sporthub.rezervation_service.dto.response.ReservationHistoryResponse;
import com.sporthub.rezervation_service.service.ReservationHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationHistoryController {
    private final ReservationHistoryService reservationHistoryService;

    public ReservationHistoryController(ReservationHistoryService reservationHistoryService) {
        this.reservationHistoryService = reservationHistoryService;
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ReservationHistoryResponse>> history(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationHistoryService.getHistoryByUser(userId));
    }
}


