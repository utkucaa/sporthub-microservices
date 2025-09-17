package com.sporthub.payment_service.repository;

import com.sporthub.payment_service.model.PaymentWebhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface PaymentWebhookRepository extends JpaRepository<PaymentWebhook, Long> {

    
    Optional<PaymentWebhook> findByStripeEventId(String stripeEventId);

    
    List<PaymentWebhook> findByEventTypeOrderByCreatedAtDesc(String eventType);

    
    List<PaymentWebhook> findByProcessedOrderByCreatedAtDesc(Boolean processed);

    List<PaymentWebhook> findWebhooksBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);

    boolean existsByStripeEventIdAndProcessed(String stripeEventId, Boolean processed);

    
    long countByProcessed(Boolean processed);

    
    long countByEventType(String eventType);

    List<PaymentWebhook> findUnprocessedWebhooksOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

    List<PaymentWebhook> findFailedWebhooks();

    void deleteOldProcessedWebhooks(@Param("cutoffDate") LocalDateTime cutoffDate);
}
