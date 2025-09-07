package com.sporthub.court_service.dto.request;

import com.sporthub.court_service.model.CourtType;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public class CourtUpdateRequest {
    
    private String name;
    
    private String description;
    
    private CourtType courtType;
    
    @Positive(message = "Price must be positive")
    private BigDecimal pricePerHour;
    
    private Boolean isIndoor;
    
    private Integer maxPlayers;
    
    private String courtSize;
    
    private String surfaceType;
    
    private Boolean lightingAvailable;
    
    private Boolean parkingAvailable;
    
    private Boolean showerAvailable;
    
    private Boolean isActive;
    
    private List<String> imageUrls;
    
    private List<String> amenityNames;
    
    public CourtUpdateRequest() {}
    
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public List<String> getImageUrls() {
        return imageUrls;
    }
    
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
    
    public List<String> getAmenityNames() {
        return amenityNames;
    }
    
    public void setAmenityNames(List<String> amenityNames) {
        this.amenityNames = amenityNames;
    }
}
