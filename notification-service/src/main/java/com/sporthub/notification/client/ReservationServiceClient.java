package com.sporthub.notification.client;

import com.sporthub.notification.dto.external.ReservationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "rezervation-service")
public interface ReservationServiceClient {

    @GetMapping("/api/reservations/{reservationId}")
    ReservationResponse getReservation(@PathVariable("reservationId") Long reservationId);
}
