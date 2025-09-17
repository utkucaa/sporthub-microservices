package com.sporthub.payment_service.repository;

import com.sporthub.payment_service.model.Payment;
import com.sporthub.payment_service.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

        
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    Optional<Payment> findByReservationId(Long reservationId);


    Page<Payment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);


    List<Payment> findByUserIdAndStatus(Long userId, PaymentStatus status);

    List<Payment> findByStatus(PaymentStatus status);

    
    List<Payment> findPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);

    
    List<Payment> findByUserIdAndDateRange(@Param("userId") Long userId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    long countByStatus(PaymentStatus status);
    
    long countByUserId(Long userId);

    boolean existsByReservationId(Long reservationId);

    List<Payment> findFailedPaymentsForRetry(@Param("cutoffDate") LocalDateTime cutoffDate);
}
