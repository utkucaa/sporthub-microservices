package com.sporthub.court_service.repository;

import com.sporthub.court_service.model.Court;
import com.sporthub.court_service.model.CourtType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {
    
    List<Court> findByCourtType(CourtType courtType);
    
    List<Court> findByIsActiveTrue();
    
    List<Court> findByIsActiveTrueAndCourtType(CourtType courtType);
    
    List<Court> findByPricePerHourBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Court> findByIsIndoor(Boolean isIndoor);
    
    List<Court> findByLightingAvailableTrue();
    
    List<Court> findByParkingAvailableTrue();
    
    List<Court> findByShowerAvailableTrue();
    
    @Query("SELECT c FROM Court c WHERE c.isActive = true ORDER BY c.rating DESC, c.totalReviews DESC")
    List<Court> findPopularCourts();
    
    @Query("SELECT c FROM Court c WHERE " +
           "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:courtType IS NULL OR c.courtType = :courtType) AND " +
           "(:minPrice IS NULL OR c.pricePerHour >= :minPrice) AND " +
           "(:maxPrice IS NULL OR c.pricePerHour <= :maxPrice) AND " +
           "(:isIndoor IS NULL OR c.isIndoor = :isIndoor) AND " +
           "(:lightingAvailable IS NULL OR c.lightingAvailable = :lightingAvailable) AND " +
           "(:parkingAvailable IS NULL OR c.parkingAvailable = :parkingAvailable) AND " +
           "(:showerAvailable IS NULL OR c.showerAvailable = :showerAvailable) AND " +
           "c.isActive = true")
    Page<Court> searchCourts(
            @Param("name") String name,
            @Param("courtType") CourtType courtType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("isIndoor") Boolean isIndoor,
            @Param("lightingAvailable") Boolean lightingAvailable,
            @Param("parkingAvailable") Boolean parkingAvailable,
            @Param("showerAvailable") Boolean showerAvailable,
            Pageable pageable
    );
    
    
    List<Court> searchByTerm(@Param("searchTerm") String searchTerm);
    
    Optional<Court> findByIdAndIsActiveTrue(Long id);
    
    Long countByCourtType(@Param("courtType") CourtType courtType);
    
    Double getAverageRatingByCourtType(@Param("courtType") CourtType courtType);
}
