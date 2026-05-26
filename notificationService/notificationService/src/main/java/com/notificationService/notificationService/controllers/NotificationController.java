package com.notificationService.notificationService.controllers;

import com.notificationService.notificationService.dtos.NotificationResponseDTO;
import com.notificationService.notificationService.services.NotificationService;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    NotificationService notificationService;

    @GetMapping(path = "/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotificationForAUser(
            @PathVariable @NotNull Long userId,
            Pageable pageable
    ) {

        return ResponseEntity.ok(notificationService.getAllNotificationForUser(userId, pageable));

    }

    @GetMapping(path = "/user/{userId}/unread")
    public ResponseEntity<Map<String, Long>> unreadNotificationsCount(
            @PathVariable @NotNull Long userId
    ) {
        long unreadNotifications = notificationService.unreadNotificationsCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", unreadNotifications));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(
            @PathVariable(name = "id") @NotNull Long notificationId
    ) {
        return ResponseEntity.ok(notificationService.getNotificationById(notificationId));
    }

    @PutMapping(path = "/{id}/read")
    public ResponseEntity<NotificationResponseDTO> updateNotificationStatus(
            @PathVariable(name = "id") @NotNull Long notificationId
            ) {

        return ResponseEntity.ok(notificationService.updateNotificationStatus(notificationId));
    }


}
