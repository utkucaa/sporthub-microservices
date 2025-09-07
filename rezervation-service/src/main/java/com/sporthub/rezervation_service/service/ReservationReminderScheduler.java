package com.sporthub.rezervation_service.service;

import com.sporthub.rezervation_service.kafka.producer.ReservationEventProducer;
import com.sporthub.rezervation_service.model.Reservation;
import com.sporthub.rezervation_service.model.ReservationStatus;
import com.sporthub.rezervation_service.repository.ReservationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationReminderScheduler {

    private final ReservationRepository reservationRepository;
    private final ReservationEventProducer eventProducer;

    public ReservationReminderScheduler(ReservationRepository reservationRepository,
                                        ReservationEventProducer eventProducer) {
        this.reservationRepository = reservationRepository;
        this.eventProducer = eventProducer;
    }

    // Every 5 minutes
    @Scheduled(fixedDelay = 300000)
    public void sendRemindersAndExpire() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> upcoming = reservationRepository.findAll();
        for (Reservation r : upcoming) {
            if (r.getStatus() == ReservationStatus.PENDING && r.getStartTime().isBefore(now)) {
                r.setStatus(ReservationStatus.EXPIRED);
                reservationRepository.save(r);
            } else if (r.getStatus() == ReservationStatus.CONFIRMED) {
                long minutes = Duration.between(now, r.getStartTime()).toMinutes();
                if (minutes == 60) {
                    eventProducer.publishNotificationRequired(r.getId(), "Reservation starts in 1 hour");
                }
            }
        }
    }
}


