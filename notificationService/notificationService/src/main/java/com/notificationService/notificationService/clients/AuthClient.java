package com.notificationService.notificationService.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "auth-client",
        url = "http://localhost:8081/api/v1/auth"
)
public interface AuthClient {

    @GetMapping("/{userId}/email")
    String getUserEmail(Long userId);
}
