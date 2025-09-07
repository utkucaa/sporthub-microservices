package com.sporthub.rezervation_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service", url = "http://localhost:8090")
public interface PaymentServiceClient {
    @PostMapping("/api/payments/charge")
    Object charge(@RequestParam("reservationId") Long reservationId,
                  @RequestParam("amount") String amount);
}


