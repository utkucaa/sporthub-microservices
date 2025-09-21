package com.sporthub.notification.repository;

import com.sporthub.notification.entity.Notification;
import com.sporthub.notification.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Long countByUserIdAndStatus(Long userId, NotificationStatus status);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByStatusAndRetryCountLessThan(NotificationStatus status, Integer maxRetryCount);

    List<Notification> findByCreatedAtBefore(LocalDateTime date);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.category = :category AND n.status = :status")
    List<Notification> findByUserIdAndCategoryAndStatus(@Param("userId") Long userId, 
                                                        @Param("category") String category, 
                                                        @Param("status") NotificationStatus status);

    @Query("SELECT n FROM Notification n WHERE n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<Notification> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
}
