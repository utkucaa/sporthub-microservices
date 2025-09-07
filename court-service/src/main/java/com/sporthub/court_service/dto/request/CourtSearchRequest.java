package com.sporthub.court_service.dto.request;

import com.sporthub.court_service.model.CourtType;

import java.math.BigDecimal;

public class CourtSearchRequest {
    
    private String name;
    
    private CourtType courtType;
    
    private BigDecimal minPrice;
    
    private BigDecimal maxPrice;
    
    private Boolean isIndoor;
    
    private Boolean lightingAvailable;
    
    private Boolean parkingAvailable;
    
    private Boolean showerAvailable;
    
    private Boolean isActive = true;
    
    private String sortBy = "name";
    
    private String sortDirection = "ASC";
    
    private Integer page = 0;
    
    private Integer size = 20;
    
    // Constructors
    public CourtSearchRequest() {}
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public CourtType getCourtType() {
        return courtType;
    }
    
    public void setCourtType(CourtType courtType) {
        this.courtType = courtType;
    }
    
    public BigDecimal getMinPrice() {
        return minPrice;
    }
    
    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }
    
    public BigDecimal getMaxPrice() {
        return maxPrice;
    }
    
    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }
    
    public Boolean getIsIndoor() {
        return isIndoor;
    }
    
    public void setIsIndoor(Boolean isIndoor) {
        this.isIndoor = isIndoor;
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
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortDirection() {
        return sortDirection;
    }
    
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
}
