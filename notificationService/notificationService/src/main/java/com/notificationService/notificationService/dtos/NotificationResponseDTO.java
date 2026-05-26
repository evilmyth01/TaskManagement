package com.notificationService.notificationService.dtos;

import com.notificationService.notificationService.enums.NotificationStatus;
import com.notificationService.notificationService.enums.NotificationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor()
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class NotificationResponseDTO {

    Long id;
    Long userId;
    String email;
    String subject;
    String body;
    NotificationType type;
    NotificationStatus status;
    Long taskId;
}
