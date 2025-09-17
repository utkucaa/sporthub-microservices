package com.sporthub.payment_service.kafka.consumer;

import com.sporthub.payment_service.dto.event.ReservationEvent;
import com.sporthub.payment_service.service.PaymentService;
import com.sporthub.payment_service.service.RefundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;


@Service
public class ReservationEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ReservationEventConsumer.class);

    private final PaymentService paymentService;
    private final RefundService refundService;

    public ReservationEventConsumer(PaymentService paymentService, RefundService refundService) {
        this.paymentService = paymentService;
        this.refundService = refundService;
    }

    /**
     * Handle reservation created events
     */
    @KafkaListener(topics = "reservation-created", groupId = "payment-service")
    public void handleReservationCreated(@Payload ReservationEvent reservationEvent,
                                        @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
                                        Acknowledgment acknowledgment) {
        try {
            logger.info("Received reservation created event: reservationId={}, userId={}, totalPrice={}", 
                       reservationEvent.getReservationId(), reservationEvent.getUserId(), 
                       reservationEvent.getTotalPrice());

            // Create payment intent for the reservation
            paymentService.createPaymentIntentForReservation(reservationEvent);

            acknowledgment.acknowledge();
            logger.info("Successfully processed reservation created event: reservationId={}", 
                       reservationEvent.getReservationId());

        } catch (Exception e) {
            logger.error("Error processing reservation created event: reservationId={}, error={}", 
                        reservationEvent.getReservationId(), e.getMessage(), e);
            
            
        }
    }

    
    @KafkaListener(topics = "reservation-cancelled", groupId = "payment-service")
    public void handleReservationCancelled(@Payload ReservationEvent reservationEvent,
                                          @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                          @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                          @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
                                          Acknowledgment acknowledgment) {
        try {
            logger.info("Received reservation cancelled event: reservationId={}, userId={}", 
                       reservationEvent.getReservationId(), reservationEvent.getUserId());

            // Process automatic refund for cancelled reservation
            refundService.processAutomaticRefund(reservationEvent);

            acknowledgment.acknowledge();
            logger.info("Successfully processed reservation cancelled event: reservationId={}", 
                       reservationEvent.getReservationId());

        } catch (Exception e) {
            logger.error("Error processing reservation cancelled event: reservationId={}, error={}", 
                        reservationEvent.getReservationId(), e.getMessage(), e);
            
        }
    }
}
