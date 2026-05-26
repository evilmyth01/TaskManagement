package com.notificationService.notificationService.services;

import com.notificationService.notificationService.dtos.NotificationResponseDTO;
import com.notificationService.notificationService.enums.NotificationStatus;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface NotificationService {

     List<NotificationResponseDTO> getAllNotificationForUser(Long userId, Pageable pageable);

     Long unreadNotificationsCount(@NotNull Long userId);

     NotificationResponseDTO getNotificationById(@NotNull Long notificationId);

     NotificationResponseDTO updateNotificationStatus(@NotNull Long notificationId);
}
