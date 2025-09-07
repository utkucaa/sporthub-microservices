package com.sporthub.rezervation_service.kafka.consumer;

import com.sporthub.rezervation_service.model.Reservation;
import com.sporthub.rezervation_service.model.ReservationStatus;
import com.sporthub.rezervation_service.repository.ReservationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentEventConsumer {

    private final ReservationRepository reservationRepository;

    public PaymentEventConsumer(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @KafkaListener(topics = "${sporthub.kafka.topic.paymentCompleted}", groupId = "reservation-service-group")
    public void onPaymentCompleted(Map<String, Object> event) {
        Long reservationId = Long.valueOf(event.get("reservationId").toString());
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation != null && reservation.getStatus() == ReservationStatus.PENDING) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
        }
    }
}


