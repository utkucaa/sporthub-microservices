package com.sporthub.payment_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaConfig {

    public static final String PAYMENT_INITIATED_TOPIC = "payment-initiated";
    public static final String PAYMENT_COMPLETED_TOPIC = "payment-completed";
    public static final String PAYMENT_FAILED_TOPIC = "payment-failed";
    public static final String REFUND_COMPLETED_TOPIC = "refund-completed";
    public static final String RESERVATION_CREATED_TOPIC = "reservation-created";
    public static final String RESERVATION_CANCELLED_TOPIC = "reservation-cancelled";

    @Bean
    public NewTopic paymentInitiatedTopic() {
        return TopicBuilder.name(PAYMENT_INITIATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentCompletedTopic() {
        return TopicBuilder.name(PAYMENT_COMPLETED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return TopicBuilder.name(PAYMENT_FAILED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic refundCompletedTopic() {
        return TopicBuilder.name(REFUND_COMPLETED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
