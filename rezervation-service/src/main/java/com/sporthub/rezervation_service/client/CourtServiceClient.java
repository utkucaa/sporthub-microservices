package com.sporthub.rezervation_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "court-service")
public interface CourtServiceClient {
    @GetMapping("/api/courts/{id}")
    Object getCourt(@PathVariable("id") Long id);
}


