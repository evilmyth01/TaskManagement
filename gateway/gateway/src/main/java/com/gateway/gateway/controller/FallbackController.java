package com.gateway.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/task")
    public ResponseEntity<Map<String, String>> taskFallback() {

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        Map.of(
                                "error",
                                "Task service is currently unavailable",

                                "code",
                                "SERVICE_UNAVAILABLE"
                        )
                );
    }

    @RequestMapping("/fallback/auth")
    public ResponseEntity<Map<String, String>> authFallback() {

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        Map.of(
                                "error",
                                "Auth service is currently unavailable",

                                "code",
                                "SERVICE_UNAVAILABLE"
                        )
                );
    }

    @RequestMapping("/fallback/analytics")
    public ResponseEntity<Map<String, String>> analyticsFallback() {

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        Map.of(
                                "error",
                                "Analytics service is currently unavailable",

                                "code",
                                "SERVICE_UNAVAILABLE"
                        )
                );
    }

    @RequestMapping("/fallback/notification")
    public ResponseEntity<Map<String, String>> NotificationFallback() {

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        Map.of(
                                "error",
                                "Notification service is currently unavailable",

                                "code",
                                "SERVICE_UNAVAILABLE"
                        )
                );
    }
}
