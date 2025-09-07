package com.sporthub.court_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courts")
public class Court {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Court name is required")
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotBlank(message = "Court description is required")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Court type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "court_type", nullable = false)
    private CourtType courtType;
    
    @Positive(message = "Price must be positive")
    @Column(name = "price_per_hour", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerHour;
    
    @Column(name = "is_indoor")
    private Boolean isIndoor = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "max_players")
    private Integer maxPlayers;
    
    @Column(name = "court_size")
    private String courtSize;
    
    @Column(name = "surface_type")
    private String surfaceType;
    
    @Column(name = "lighting_available")
    private Boolean lightingAvailable = false;
    
    @Column(name = "parking_available")
    private Boolean parkingAvailable = false;
    
    @Column(name = "shower_available")
    private Boolean showerAvailable = false;
    
    @Column(name = "rating")
    private Double rating = 0.0;
    
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourtImage> images = new ArrayList<>();
    
    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourtAmenity> amenities = new ArrayList<>();
    
    // Constructors
    public Court() {}
    
    public Court(String name, String description, CourtType courtType, BigDecimal pricePerHour) {
        this.name = name;
        this.description = description;
        this.courtType = courtType;
        this.pricePerHour = pricePerHour;
    }
    
    // Getters and Setters
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
    
    public List<CourtImage> getImages() {
        return images;
    }
    
    public void setImages(List<CourtImage> images) {
        this.images = images;
    }
    
    public List<CourtAmenity> getAmenities() {
        return amenities;
    }
    
    public void setAmenities(List<CourtAmenity> amenities) {
        this.amenities = amenities;
    }
    
    // Helper methods
    public void addImage(CourtImage image) {
        images.add(image);
        image.setCourt(this);
    }
    
    public void removeImage(CourtImage image) {
        images.remove(image);
        image.setCourt(null);
    }
    
    public void addAmenity(CourtAmenity amenity) {
        amenities.add(amenity);
        amenity.setCourt(this);
    }
    
    public void removeAmenity(CourtAmenity amenity) {
        amenities.remove(amenity);
        amenity.setCourt(null);
    }
    
    @Override
    public String toString() {
        return "Court{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", courtType=" + courtType +
                ", pricePerHour=" + pricePerHour +
                ", isActive=" + isActive +
                ", rating=" + rating +
                '}';
    }
}
