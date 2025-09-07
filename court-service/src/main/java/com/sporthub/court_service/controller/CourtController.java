package com.sporthub.court_service.controller;

import com.sporthub.court_service.dto.request.CourtCreateRequest;
import com.sporthub.court_service.dto.request.CourtSearchRequest;
import com.sporthub.court_service.dto.request.CourtUpdateRequest;
import com.sporthub.court_service.dto.response.CourtResponse;
import com.sporthub.court_service.model.CourtType;
import com.sporthub.court_service.service.CourtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courts")
@CrossOrigin(origins = "*")
public class CourtController {
    
    @Autowired
    private CourtService courtService;
    
    @PostMapping
    public ResponseEntity<CourtResponse> createCourt(@Valid @RequestBody CourtCreateRequest request) {
        CourtResponse court = courtService.createCourt(request);
        return new ResponseEntity<>(court, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CourtResponse> getCourtById(@PathVariable Long id) {
        CourtResponse court = courtService.getCourtById(id);
        return ResponseEntity.ok(court);
    }
    
    @GetMapping
    public ResponseEntity<List<CourtResponse>> getAllCourts() {
        List<CourtResponse> courts = courtService.getAllCourts();
        return ResponseEntity.ok(courts);
    }
    
    @GetMapping("/type/{courtType}")
    public ResponseEntity<List<CourtResponse>> getCourtsByType(@PathVariable CourtType courtType) {
        List<CourtResponse> courts = courtService.getCourtsByType(courtType);
        return ResponseEntity.ok(courts);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<CourtResponse>> getActiveCourts() {
        List<CourtResponse> courts = courtService.getActiveCourts();
        return ResponseEntity.ok(courts);
    }
    
    @GetMapping("/popular")
    public ResponseEntity<List<CourtResponse>> getPopularCourts() {
        List<CourtResponse> courts = courtService.getPopularCourts();
        return ResponseEntity.ok(courts);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CourtResponse> updateCourt(@PathVariable Long id, @Valid @RequestBody CourtUpdateRequest request) {
        CourtResponse court = courtService.updateCourt(id, request);
        return ResponseEntity.ok(court);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourt(@PathVariable Long id) {
        courtService.deleteCourt(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateCourt(@PathVariable Long id) {
        courtService.deactivateCourt(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateCourt(@PathVariable Long id) {
        courtService.activateCourt(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/search")
    public ResponseEntity<Page<CourtResponse>> searchCourts(@RequestBody CourtSearchRequest request) {
        Page<CourtResponse> courts = courtService.searchCourts(request);
        return ResponseEntity.ok(courts);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<CourtResponse>> searchCourtsByTerm(@RequestParam String term) {
        List<CourtResponse> courts = courtService.searchCourtsByTerm(term);
        return ResponseEntity.ok(courts);
    }
    
    @GetMapping("/search/price")
    public ResponseEntity<List<CourtResponse>> getCourtsByPriceRange(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        List<CourtResponse> courts = courtService.getCourtsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(courts);
    }
    
    @GetMapping("/search/amenities")
    public ResponseEntity<List<CourtResponse>> getCourtsByAmenities(
            @RequestParam(required = false) Boolean lighting,
            @RequestParam(required = false) Boolean parking,
            @RequestParam(required = false) Boolean shower) {
        List<CourtResponse> courts = courtService.getCourtsByAmenities(lighting, parking, shower);
        return ResponseEntity.ok(courts);
    }
    
    @GetMapping("/stats/count/{courtType}")
    public ResponseEntity<Long> getCourtCountByType(@PathVariable CourtType courtType) {
        Long count = courtService.getCourtCountByType(courtType);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/rating/{courtType}")
    public ResponseEntity<Double> getAverageRatingByCourtType(@PathVariable CourtType courtType) {
        Double rating = courtService.getAverageRatingByCourtType(courtType);
        return ResponseEntity.ok(rating);
    }
    
    @PostMapping("/{id}/rating")
    public ResponseEntity<Void> updateCourtRating(@PathVariable Long id, @RequestParam Double rating) {
        courtService.updateCourtRating(id, rating);
        return ResponseEntity.ok().build();
    }
}
