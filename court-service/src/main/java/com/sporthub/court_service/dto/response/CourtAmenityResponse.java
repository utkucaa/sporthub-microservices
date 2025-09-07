package com.sporthub.court_service.dto.response;

import java.time.LocalDateTime;

public class CourtAmenityResponse {
    
    private Long id;
    
    private String amenityName;
    
    private String amenityDescription;
    
    private Boolean isAvailable;
    
    private String amenityIcon;
    
    private LocalDateTime createdAt;
    
    public CourtAmenityResponse() {}
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAmenityName() {
        return amenityName;
    }
    
    public void setAmenityName(String amenityName) {
        this.amenityName = amenityName;
    }
    
    public String getAmenityDescription() {
        return amenityDescription;
    }
    
    public void setAmenityDescription(String amenityDescription) {
        this.amenityDescription = amenityDescription;
    }
    
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
    public String getAmenityIcon() {
        return amenityIcon;
    }
    
    public void setAmenityIcon(String amenityIcon) {
        this.amenityIcon = amenityIcon;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
