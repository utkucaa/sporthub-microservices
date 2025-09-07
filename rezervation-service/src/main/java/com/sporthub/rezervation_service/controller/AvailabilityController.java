package com.sporthub.rezervation_service.controller;

import com.sporthub.rezervation_service.dto.response.AvailabilitySlotResponse;
import com.sporthub.rezervation_service.service.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/court/{courtId}/availability")
    public ResponseEntity<List<AvailabilitySlotResponse>> availability(
            @PathVariable Long courtId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(availabilityService.getCourtAvailability(courtId, date));
    }

    @PostMapping("/bulk-check")
    public ResponseEntity<List<AvailabilitySlotResponse>> bulkCheck(
            @RequestParam Long courtId,
            @RequestBody List<String[]> rangesIso) {
        List<LocalDateTime[]> ranges = new ArrayList<>();
        for (String[] pair : rangesIso) {
            ranges.add(new LocalDateTime[]{LocalDateTime.parse(pair[0]), LocalDateTime.parse(pair[1])});
        }
        return ResponseEntity.ok(availabilityService.bulkCheck(courtId, ranges));
    }
}


