package com.sporthub.payment_service.repository;

import com.sporthub.payment_service.model.Refund;
import com.sporthub.payment_service.model.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {

    
    Optional<Refund> findByStripeRefundId(String stripeRefundId);


    List<Refund> findByPaymentIdOrderByCreatedAtDesc(Long paymentId);
       
    List<Refund> findByPaymentIdAndStatus(Long paymentId, RefundStatus status);

    List<Refund> findByStatus(RefundStatus status);

    List<Refund> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);


    List<Refund> findByInitiatedByOrderByCreatedAtDesc(String initiatedBy);


    long countByStatus(RefundStatus status);


    long countByPaymentId(Long paymentId);


    boolean existsByPaymentIdAndStatus(Long paymentId, RefundStatus status);


    List<Refund> findByStatusAndCreatedAtBefore(RefundStatus status, LocalDateTime cutoffDate);
}
