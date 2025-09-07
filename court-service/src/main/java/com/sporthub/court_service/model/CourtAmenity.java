package com.sporthub.court_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "court_amenities")
public class CourtAmenity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Amenity name is required")
    @Column(name = "amenity_name", nullable = false)
    private String amenityName;
    
    @Column(name = "amenity_description")
    private String amenityDescription;
    
    @Column(name = "is_available")
    private Boolean isAvailable = true;
    
    @Column(name = "amenity_icon")
    private String amenityIcon;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;
    
    // Constructors
    public CourtAmenity() {}
    
    public CourtAmenity(String amenityName, String amenityDescription) {
        this.amenityName = amenityName;
        this.amenityDescription = amenityDescription;
    }
    
    // Getters and Setters
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
    
    public Court getCourt() {
        return court;
    }
    
    public void setCourt(Court court) {
        this.court = court;
    }
    
    @Override
    public String toString() {
        return "CourtAmenity{" +
                "id=" + id +
                ", amenityName='" + amenityName + '\'' +
                ", amenityDescription='" + amenityDescription + '\'' +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
