package com.sporthub.rezervation_service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic topicReservationCreated(@Value("${sporthub.kafka.topic.reservationCreated}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic topicReservationConfirmed(@Value("${sporthub.kafka.topic.reservationConfirmed}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic topicReservationCancelled(@Value("${sporthub.kafka.topic.reservationCancelled}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic topicPaymentCompleted(@Value("${sporthub.kafka.topic.paymentCompleted}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic topicNotificationRequired(@Value("${sporthub.kafka.topic.notificationRequired}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}


