package com.sporthub.court_service.service;

import com.sporthub.court_service.dto.request.CourtCreateRequest;
import com.sporthub.court_service.dto.request.CourtSearchRequest;
import com.sporthub.court_service.dto.request.CourtUpdateRequest;
import com.sporthub.court_service.dto.response.CourtResponse;
import com.sporthub.court_service.model.CourtType;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CourtService {
    
    CourtResponse createCourt(CourtCreateRequest request);
    
    CourtResponse getCourtById(Long id);
    
    List<CourtResponse> getAllCourts();
    
    List<CourtResponse> getCourtsByType(CourtType courtType);
    
    List<CourtResponse> getActiveCourts();
    
    List<CourtResponse> getPopularCourts();
    
    CourtResponse updateCourt(Long id, CourtUpdateRequest request);
    
    void deleteCourt(Long id);
    
    void deactivateCourt(Long id);
    
    void activateCourt(Long id);
    
    Page<CourtResponse> searchCourts(CourtSearchRequest request);
    
    List<CourtResponse> searchCourtsByTerm(String searchTerm);
    
    List<CourtResponse> getCourtsByPriceRange(Double minPrice, Double maxPrice);
    
    List<CourtResponse> getCourtsByAmenities(Boolean lighting, Boolean parking, Boolean shower);
    
    Long getCourtCountByType(CourtType courtType);
    
    Double getAverageRatingByCourtType(CourtType courtType);
    
    void updateCourtRating(Long courtId, Double rating);
}
