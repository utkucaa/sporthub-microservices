package com.sporthub.court_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "court_images")
public class CourtImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Image URL is required")
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    
    @Column(name = "image_title")
    private String imageTitle;
    
    @Column(name = "is_primary")
    private Boolean isPrimary = false;
    
    @Column(name = "image_order")
    private Integer imageOrder = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;
    
    // Constructors
    public CourtImage() {}
    
    public CourtImage(String imageUrl, String imageTitle) {
        this.imageUrl = imageUrl;
        this.imageTitle = imageTitle;
    }
    
    // Getters and Setters
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
    
    public Court getCourt() {
        return court;
    }
    
    public void setCourt(Court court) {
        this.court = court;
    }
    
    @Override
    public String toString() {
        return "CourtImage{" +
                "id=" + id +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageTitle='" + imageTitle + '\'' +
                ", isPrimary=" + isPrimary +
                '}';
    }
}
