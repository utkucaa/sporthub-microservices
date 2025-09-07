package com.sporthub.api_gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service-users", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .rewritePath("/api/users/(?<segment>.*)", "/api/users/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("lb://USER-SERVICE"))
                .route("user-service-auth", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .rewritePath("/api/auth/(?<segment>.*)", "/api/auth/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("lb://USER-SERVICE"))
                .route("court-service", r -> r
                        .path("/api/courts/**")
                        .filters(f -> f
                                .rewritePath("/api/courts/(?<segment>.*)", "/api/courts/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("lb://COURT-SERVICE"))
                .route("reservation-service", r -> r
                        .path("/api/reservations/**")
                        .filters(f -> f
                                .rewritePath("/api/reservations/(?<segment>.*)", "/api/reservations/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("lb://RESERVATION-SERVICE"))
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .filters(f -> f
                                .rewritePath("/api/payments/(?<segment>.*)", "/api/payments/${segment}")
                                .addRequestHeader("X-Response-Time", System.currentTimeMillis() + ""))
                        .uri("lb://PAYMENT-SERVICE"))
                .build();
    }
}
