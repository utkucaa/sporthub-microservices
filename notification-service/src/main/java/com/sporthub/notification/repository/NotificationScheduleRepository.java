package com.sporthub.notification.repository;

import com.sporthub.notification.entity.NotificationSchedule;
import com.sporthub.notification.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationScheduleRepository extends JpaRepository<NotificationSchedule, Long> {

    List<NotificationSchedule> findByReservationId(Long reservationId);

    List<NotificationSchedule> findByUserId(Long userId);

    @Query("SELECT ns FROM NotificationSchedule ns WHERE ns.scheduledTime <= :now AND ns.status = :status")
    List<NotificationSchedule> findDueSchedules(@Param("now") LocalDateTime now, 
                                               @Param("status") NotificationStatus status);

    List<NotificationSchedule> findByStatus(NotificationStatus status);

    List<NotificationSchedule> findByReservationIdAndStatus(Long reservationId, NotificationStatus status);

    @Query("SELECT ns FROM NotificationSchedule ns WHERE ns.scheduledTime BETWEEN :startTime AND :endTime")
    List<NotificationSchedule> findByScheduledTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                                         @Param("endTime") LocalDateTime endTime);
}
