package com.sporthub.court_service.service.impl;

import com.sporthub.court_service.dto.request.CourtCreateRequest;
import com.sporthub.court_service.dto.request.CourtSearchRequest;
import com.sporthub.court_service.dto.request.CourtUpdateRequest;
import com.sporthub.court_service.dto.response.CourtAmenityResponse;
import com.sporthub.court_service.dto.response.CourtImageResponse;
import com.sporthub.court_service.dto.response.CourtResponse;
import com.sporthub.court_service.model.Court;
import com.sporthub.court_service.model.CourtAmenity;
import com.sporthub.court_service.model.CourtImage;
import com.sporthub.court_service.model.CourtType;
import com.sporthub.court_service.repository.CourtRepository;
import com.sporthub.court_service.service.CourtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

// saha işlemlerini yöneten ana servis - redis cache ile
@Service
@Transactional
public class CourtServiceImpl implements CourtService {
    
    @Autowired
    private CourtRepository courtRepository;
    
    // yeni saha oluştur ve cache temizle
    @Override
    @CacheEvict(value = {"courts", "popularCourts"}, allEntries = true)
    public CourtResponse createCourt(CourtCreateRequest request) {
        Court court = new Court();
        court.setName(request.getName());
        court.setDescription(request.getDescription());
        court.setCourtType(request.getCourtType());
        court.setPricePerHour(request.getPricePerHour());
        court.setIsIndoor(request.getIsIndoor());
        court.setMaxPlayers(request.getMaxPlayers());
        court.setCourtSize(request.getCourtSize());
        court.setSurfaceType(request.getSurfaceType());
        court.setLightingAvailable(request.getLightingAvailable());
        court.setParkingAvailable(request.getParkingAvailable());
        court.setShowerAvailable(request.getShowerAvailable());
        
        
        if (request.getImageUrls() != null) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                String imageUrl = request.getImageUrls().get(i);
                CourtImage image = new CourtImage(imageUrl, "Court Image " + (i + 1));
                image.setIsPrimary(i == 0); 
                image.setImageOrder(i);
                court.addImage(image);
            }
        }
        
        
        if (request.getAmenityNames() != null) {
            for (String amenityName : request.getAmenityNames()) {
                CourtAmenity amenity = new CourtAmenity(amenityName, "Available " + amenityName);
                court.addAmenity(amenity);
            }
        }
        
        Court savedCourt = courtRepository.save(court);
        return convertToResponse(savedCourt);
    }
    
    @Override
    @Cacheable(value = "courts", key = "#id")
    public CourtResponse getCourtById(Long id) {
        Court court = courtRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Court not found with id: " + id));
        return convertToResponse(court);
    }
    
    @Override
    @Cacheable(value = "courts", key = "'all'")
    public List<CourtResponse> getAllCourts() {
        List<Court> courts = courtRepository.findByIsActiveTrue();
        return courts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Cacheable(value = "courts", key = "'type_' + #courtType")
    public List<CourtResponse> getCourtsByType(CourtType courtType) {
        List<Court> courts = courtRepository.findByIsActiveTrueAndCourtType(courtType);
        return courts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Cacheable(value = "courts", key = "'active'")
    public List<CourtResponse> getActiveCourts() {
        List<Court> courts = courtRepository.findByIsActiveTrue();
        return courts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Cacheable(value = "popularCourts")
    public List<CourtResponse> getPopularCourts() {
        List<Court> courts = courtRepository.findPopularCourts();
        return courts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @CacheEvict(value = {"courts", "popularCourts"}, allEntries = true)
    public CourtResponse updateCourt(Long id, CourtUpdateRequest request) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Court not found with id: " + id));
        
        if (request.getName() != null) court.setName(request.getName());
        if (request.getDescription() != null) court.setDescription(request.getDescription());
        if (request.getCourtType() != null) court.setCourtType(request.getCourtType());
        if (request.getPricePerHour() != null) court.setPricePerHour(request.getPricePerHour());
        if (request.getIsIndoor() != null) court.setIsIndoor(request.getIsIndoor());
        if (request.getMaxPlayers() != null) court.setMaxPlayers(request.getMaxPlayers());
        if (request.getCourtSize() != null) court.setCourtSize(request.getCourtSize());
        if (request.getSurfaceType() != null) court.setSurfaceType(request.getSurfaceType());
        if (request.getLightingAvailable() != null) court.setLightingAvailable(request.getLightingAvailable());
        if (request.getParkingAvailable() != null) court.setParkingAvailable(request.getParkingAvailable());
        if (request.getShowerAvailable() != null) court.setShowerAvailable(request.getShowerAvailable());
        if (request.getIsActive() != null) court.setIsActive(request.getIsActive());
        
        
        if (request.getImageUrls() != null) {
            court.getImages().clear();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                String imageUrl = request.getImageUrls().get(i);
                CourtImage image = new CourtImage(imageUrl, "Court Image " + (i + 1));
                image.setIsPrimary(i == 0);
                image.setImageOrder(i);
                court.addImage(image);
            }
        }
        
        
        if (request.getAmenityNames() != null) {
            court.getAmenities().clear();
            for (String amenityName : request.getAmenityNames()) {
                CourtAmenity amenity = new CourtAmenity(amenityName, "Available " + amenityName);
                court.addAmenity(amenity);
            }
        }
        
        Court updatedCourt = courtRepository.save(court);
        return convertToResponse(updatedCourt);
    }
    
    @Override
    @CacheEvict(value = {"courts", "popularCourts"}, allEntries = true)
    public void deleteCourt(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Court not found with id: " + id));
        courtRepository.delete(court);
    }
    
    @Override
    @CacheEvict(value = {"courts", "popularCourts"}, allEntries = true)
    public void deactivateCourt(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Court not found with id: " + id));
        court.setIsActive(false);
        courtRepository.save(court);
    }
    
    @Override
    @CacheEvict(value = {"courts", "popularCourts"}, allEntries = true)
    public void activateCourt(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Court not found with id: " + id));
        court.setIsActive(true);
        courtRepository.save(court);
    }
    
    @Override
    public Page<CourtResponse> searchCourts(CourtSearchRequest request) {
        Sort sort = Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        Page<Court> courts = courtRepository.searchCourts(
                request.getName(),
                request.getCourtType(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getIsIndoor(),
                request.getLightingAvailable(),
                request.getParkingAvailable(),
                request.getShowerAvailable(),
                pageable
        );
        
        return courts.map(this::convertToResponse);
    }
    
    @Override
    public List<CourtResponse> searchCourtsByTerm(String searchTerm) {
        List<Court> courts = courtRepository.findByIsActiveTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm);
        return courts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CourtResponse> getCourtsByPriceRange(Double minPrice, Double maxPrice) {
        BigDecimal min = minPrice != null ? BigDecimal.valueOf(minPrice) : null;
        BigDecimal max = maxPrice != null ? BigDecimal.valueOf(maxPrice) : null;
        
        List<Court> courts = courtRepository.findByPricePerHourBetween(min, max);
        return courts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CourtResponse> getCourtsByAmenities(Boolean lighting, Boolean parking, Boolean shower) {
        List<Court> courts = courtRepository.findByIsActiveTrue();
        
        return courts.stream()
                .filter(court -> lighting == null || court.getLightingAvailable().equals(lighting))
                .filter(court -> parking == null || court.getParkingAvailable().equals(parking))
                .filter(court -> shower == null || court.getShowerAvailable().equals(shower))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public Long getCourtCountByType(CourtType courtType) {
        return courtRepository.countByCourtType(courtType);
    }
    
    @Override
    public Double getAverageRatingByCourtType(CourtType courtType) {
        List<Court> courts = courtRepository.findByCourtType(courtType);
        return courts.stream()
                .filter(court -> court.getTotalReviews() > 0)
                .mapToDouble(Court::getRating)
                .average()
                .orElse(0.0);
    }
    
    @Override
    @CacheEvict(value = {"courts", "popularCourts"}, allEntries = true)
    public void updateCourtRating(Long courtId, Double rating) {
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new RuntimeException("Court not found with id: " + courtId));
        
        double currentRating = court.getRating();
        int currentReviews = court.getTotalReviews();
        
        double newRating = ((currentRating * currentReviews) + rating) / (currentReviews + 1);
        court.setRating(newRating);
        court.setTotalReviews(currentReviews + 1);
        
        courtRepository.save(court);
    }
    
    private CourtResponse convertToResponse(Court court) {
        CourtResponse response = new CourtResponse();
        response.setId(court.getId());
        response.setName(court.getName());
        response.setDescription(court.getDescription());
        response.setCourtType(court.getCourtType());
        response.setPricePerHour(court.getPricePerHour());
        response.setIsIndoor(court.getIsIndoor());
        response.setIsActive(court.getIsActive());
        response.setMaxPlayers(court.getMaxPlayers());
        response.setCourtSize(court.getCourtSize());
        response.setSurfaceType(court.getSurfaceType());
        response.setLightingAvailable(court.getLightingAvailable());
        response.setParkingAvailable(court.getParkingAvailable());
        response.setShowerAvailable(court.getShowerAvailable());
        response.setRating(court.getRating());
        response.setTotalReviews(court.getTotalReviews());
        response.setCreatedAt(court.getCreatedAt());
        response.setUpdatedAt(court.getUpdatedAt());
        
        
        if (court.getImages() != null) {
            List<CourtImageResponse> imageResponses = court.getImages().stream()
                    .map(this::convertToImageResponse)
                    .collect(Collectors.toList());
            response.setImages(imageResponses);
        }
        
            
        if (court.getAmenities() != null) {
            List<CourtAmenityResponse> amenityResponses = court.getAmenities().stream()
                    .map(this::convertToAmenityResponse)
                    .collect(Collectors.toList());
            response.setAmenities(amenityResponses);
        }
        
        return response;
    }
    
    private CourtImageResponse convertToImageResponse(CourtImage image) {
        CourtImageResponse response = new CourtImageResponse();
        response.setId(image.getId());
        response.setImageUrl(image.getImageUrl());
        response.setImageTitle(image.getImageTitle());
        response.setIsPrimary(image.getIsPrimary());
        response.setImageOrder(image.getImageOrder());
        response.setCreatedAt(image.getCreatedAt());
        return response;
    }
    
    private CourtAmenityResponse convertToAmenityResponse(CourtAmenity amenity) {
        CourtAmenityResponse response = new CourtAmenityResponse();
        response.setId(amenity.getId());
        response.setAmenityName(amenity.getAmenityName());
        response.setAmenityDescription(amenity.getAmenityDescription());
        response.setIsAvailable(amenity.getIsAvailable());
        response.setAmenityIcon(amenity.getAmenityIcon());
        response.setCreatedAt(amenity.getCreatedAt());
        return response;
    }
}
