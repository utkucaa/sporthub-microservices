package com.sporthub.notification.client;

import com.sporthub.notification.dto.external.CourtResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "court-service")
public interface CourtServiceClient {

    @GetMapping("/api/courts/{courtId}")
    CourtResponse getCourt(@PathVariable("courtId") Long courtId);
}
