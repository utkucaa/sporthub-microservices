package com.sporthub.rezervation_service.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReservationEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topicCreated;
    private final String topicConfirmed;
    private final String topicCancelled;
    private final String topicNotification;

    public ReservationEventProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                    @Value("${sporthub.kafka.topic.reservationCreated}") String topicCreated,
                                    @Value("${sporthub.kafka.topic.reservationConfirmed}") String topicConfirmed,
                                    @Value("${sporthub.kafka.topic.reservationCancelled}") String topicCancelled,
                                    @Value("${sporthub.kafka.topic.notificationRequired}") String topicNotification) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicCreated = topicCreated;
        this.topicConfirmed = topicConfirmed;
        this.topicCancelled = topicCancelled;
        this.topicNotification = topicNotification;
    }

    public void publishCreated(Long reservationId, Long userId, Long courtId) {
        kafkaTemplate.send(topicCreated, Map.of("reservationId", reservationId, "userId", userId, "courtId", courtId));
    }

    public void publishConfirmed(Long reservationId) {
        kafkaTemplate.send(topicConfirmed, Map.of("reservationId", reservationId));
    }

    public void publishCancelled(Long reservationId) {
        kafkaTemplate.send(topicCancelled, Map.of("reservationId", reservationId));
    }

    public void publishNotificationRequired(Long reservationId, String message) {
        kafkaTemplate.send(topicNotification, Map.of("reservationId", reservationId, "message", message));
    }
}


