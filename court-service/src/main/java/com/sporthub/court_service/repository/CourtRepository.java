package com.sporthub.court_service.repository;

import com.sporthub.court_service.model.Court;
import com.sporthub.court_service.model.CourtType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

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
    
    List<Court> findByIsActiveTrueOrderByRatingDescTotalReviewsDesc();

    default List<Court> findPopularCourts() {
        return findByIsActiveTrueOrderByRatingDescTotalReviewsDesc();
    }
    
    default Page<Court> searchCourts(
            String name,
            CourtType courtType,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean isIndoor,
            Boolean lightingAvailable,
            Boolean parkingAvailable,
            Boolean showerAvailable,
            Pageable pageable
    ) {
        List<Court> filtered = findAll().stream()
                .filter(c -> name == null || (c.getName() != null && c.getName().toLowerCase().contains(name.toLowerCase())))
                .filter(c -> courtType == null || c.getCourtType() == courtType)
                .filter(c -> minPrice == null || (c.getPricePerHour() != null && c.getPricePerHour().compareTo(minPrice) >= 0))
                .filter(c -> maxPrice == null || (c.getPricePerHour() != null && c.getPricePerHour().compareTo(maxPrice) <= 0))
                .filter(c -> isIndoor == null || Objects.equals(c.getIsIndoor(), isIndoor))
                .filter(c -> lightingAvailable == null || Objects.equals(c.getLightingAvailable(), lightingAvailable))
                .filter(c -> parkingAvailable == null || Objects.equals(c.getParkingAvailable(), parkingAvailable))
                .filter(c -> showerAvailable == null || Objects.equals(c.getShowerAvailable(), showerAvailable))
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<Court> pageContent = start >= filtered.size() ? Collections.emptyList() : filtered.subList(start, end);
        return new PageImpl<>(pageContent, pageable, filtered.size());
    }
    
    
    // Simple search by name or description - Spring Data will handle this automatically
    List<Court> findByIsActiveTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    
    Optional<Court> findByIdAndIsActiveTrue(Long id);
    
    Long countByCourtType(CourtType courtType);
}
