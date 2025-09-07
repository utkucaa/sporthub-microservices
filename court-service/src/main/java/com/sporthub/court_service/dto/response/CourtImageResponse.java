package com.sporthub.court_service.dto.response;

import java.time.LocalDateTime;

public class CourtImageResponse {
    
    private Long id;
    
    private String imageUrl;
    
    private String imageTitle;
    
    private Boolean isPrimary;
    
    private Integer imageOrder;
    
    private LocalDateTime createdAt;
    
    public CourtImageResponse() {}
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getImageTitle() {
        return imageTitle;
    }
    
    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }
    
    public Boolean getIsPrimary() {
        return isPrimary;
    }
    
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
    
    public Integer getImageOrder() {
        return imageOrder;
    }
    
    public void setImageOrder(Integer imageOrder) {
        this.imageOrder = imageOrder;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
