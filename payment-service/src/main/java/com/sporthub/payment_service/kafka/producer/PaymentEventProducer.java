package com.sporthub.payment_service.kafka.producer;

import com.sporthub.payment_service.config.KafkaConfig;
import com.sporthub.payment_service.dto.event.PaymentEvent;
import com.sporthub.payment_service.dto.event.RefundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka producer for payment events
 */
@Service
public class PaymentEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publish payment initiated event
     */
    public void publishPaymentInitiated(PaymentEvent paymentEvent) {
        try {
            String key = "payment-" + paymentEvent.getPaymentId();
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(KafkaConfig.PAYMENT_INITIATED_TOPIC, key, paymentEvent);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Payment initiated event sent successfully: paymentId={}, reservationId={}", 
                               paymentEvent.getPaymentId(), paymentEvent.getReservationId());
                } else {
                    logger.error("Failed to send payment initiated event: paymentId={}, error={}", 
                                paymentEvent.getPaymentId(), ex.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error("Error publishing payment initiated event: paymentId={}, error={}", 
                        paymentEvent.getPaymentId(), e.getMessage(), e);
        }
    }

    /**
     * Publish payment completed event
     */
    public void publishPaymentCompleted(PaymentEvent paymentEvent) {
        try {
            String key = "payment-" + paymentEvent.getPaymentId();
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(KafkaConfig.PAYMENT_COMPLETED_TOPIC, key, paymentEvent);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Payment completed event sent successfully: paymentId={}, reservationId={}", 
                               paymentEvent.getPaymentId(), paymentEvent.getReservationId());
                } else {
                    logger.error("Failed to send payment completed event: paymentId={}, error={}", 
                                paymentEvent.getPaymentId(), ex.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error("Error publishing payment completed event: paymentId={}, error={}", 
                        paymentEvent.getPaymentId(), e.getMessage(), e);
        }
    }

    /**
     * Publish payment failed event
     */
    public void publishPaymentFailed(PaymentEvent paymentEvent) {
        try {
            String key = "payment-" + paymentEvent.getPaymentId();
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(KafkaConfig.PAYMENT_FAILED_TOPIC, key, paymentEvent);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Payment failed event sent successfully: paymentId={}, reservationId={}", 
                               paymentEvent.getPaymentId(), paymentEvent.getReservationId());
                } else {
                    logger.error("Failed to send payment failed event: paymentId={}, error={}", 
                                paymentEvent.getPaymentId(), ex.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error("Error publishing payment failed event: paymentId={}, error={}", 
                        paymentEvent.getPaymentId(), e.getMessage(), e);
        }
    }

    /**
     * Publish refund completed event
     */
    public void publishRefundCompleted(RefundEvent refundEvent) {
        try {
            String key = "refund-" + refundEvent.getRefundId();
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(KafkaConfig.REFUND_COMPLETED_TOPIC, key, refundEvent);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Refund completed event sent successfully: refundId={}, paymentId={}", 
                               refundEvent.getRefundId(), refundEvent.getPaymentId());
                } else {
                    logger.error("Failed to send refund completed event: refundId={}, error={}", 
                                refundEvent.getRefundId(), ex.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error("Error publishing refund completed event: refundId={}, error={}", 
                        refundEvent.getRefundId(), e.getMessage(), e);
        }
    }
}
