package com.sporthub.court_service.dto.response;

import com.sporthub.court_service.model.CourtType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CourtResponse {
    
    private Long id;
    
    private String name;
    
    private String description;
    
    private CourtType courtType;
    
    private BigDecimal pricePerHour;
    
    private Boolean isIndoor;
    
    private Boolean isActive;
    
    private Integer maxPlayers;
    
    private String courtSize;
    
    private String surfaceType;
    
    private Boolean lightingAvailable;
    
    private Boolean parkingAvailable;
    
    private Boolean showerAvailable;
    
    private Double rating;
    
    private Integer totalReviews;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private List<CourtImageResponse> images;
    
    private List<CourtAmenityResponse> amenities;
    
    public CourtResponse() {}
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public CourtType getCourtType() {
        return courtType;
    }
    
    public void setCourtType(CourtType courtType) {
        this.courtType = courtType;
    }
    
    public BigDecimal getPricePerHour() {
        return pricePerHour;
    }
    
    public void setPricePerHour(BigDecimal pricePerHour) {
        this.pricePerHour = pricePerHour;
    }
    
    public Boolean getIsIndoor() {
        return isIndoor;
    }
    
    public void setIsIndoor(Boolean isIndoor) {
        this.isIndoor = isIndoor;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getMaxPlayers() {
        return maxPlayers;
    }
    
    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
    
    public String getCourtSize() {
        return courtSize;
    }
    
    public void setCourtSize(String courtSize) {
        this.courtSize = courtSize;
    }
    
    public String getSurfaceType() {
        return surfaceType;
    }
    
    public void setSurfaceType(String surfaceType) {
        this.surfaceType = surfaceType;
    }
    
    public Boolean getLightingAvailable() {
        return lightingAvailable;
    }
    
    public void setLightingAvailable(Boolean lightingAvailable) {
        this.lightingAvailable = lightingAvailable;
    }
    
    public Boolean getParkingAvailable() {
        return parkingAvailable;
    }
    
    public void setParkingAvailable(Boolean parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }
    
    public Boolean getShowerAvailable() {
        return showerAvailable;
    }
    
    public void setShowerAvailable(Boolean showerAvailable) {
        this.showerAvailable = showerAvailable;
    }
    
    public Double getRating() {
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
    
    public Integer getTotalReviews() {
        return totalReviews;
    }
    
    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<CourtImageResponse> getImages() {
        return images;
    }
    
    public void setImages(List<CourtImageResponse> images) {
        this.images = images;
    }
    
    public List<CourtAmenityResponse> getAmenities() {
        return amenities;
    }
    
    public void setAmenities(List<CourtAmenityResponse> amenities) {
        this.amenities = amenities;
    }
}
