package com.sporthub.rezervation_service.controller;

import com.sporthub.rezervation_service.dto.request.CreateReservationRequest;
import com.sporthub.rezervation_service.dto.request.UpdateReservationRequest;
import com.sporthub.rezervation_service.dto.response.ReservationResponse;
import com.sporthub.rezervation_service.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

// rezervasyon işlemlerini yöneten rest controller
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // yeni rezervasyon oluştur
    @PostMapping
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody CreateReservationRequest request) {
        return ResponseEntity.ok(reservationService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.get(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateReservationRequest request) {
        return ResponseEntity.ok(reservationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id,
                                       @RequestHeader(value = "X-User-Id", required = false) Long performedBy) {
        reservationService.cancel(id, performedBy);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/extend")
    public ResponseEntity<ReservationResponse> extend(@PathVariable Long id,
                                                      @RequestParam("newEndTime") String newEndTime,
                                                      @RequestHeader(value = "X-User-Id", required = false) Long performedBy) {
        return ResponseEntity.ok(reservationService.extend(id, LocalDateTime.parse(newEndTime), performedBy));
    }
}


