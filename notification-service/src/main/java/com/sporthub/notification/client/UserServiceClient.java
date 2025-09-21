package com.sporthub.notification.client;

import com.sporthub.notification.dto.external.UserResponse;
import com.sporthub.notification.dto.external.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}")
    UserResponse getUser(@PathVariable("userId") Long userId);

    @GetMapping("/api/users/{userId}/profile")
    UserProfileResponse getUserProfile(@PathVariable("userId") Long userId);
}
