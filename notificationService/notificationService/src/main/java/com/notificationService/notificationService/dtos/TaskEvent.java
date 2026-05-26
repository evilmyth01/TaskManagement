package com.notificationService.notificationService.dtos;

import com.notificationService.notificationService.enums.TaskEventType;
import com.notificationService.notificationService.enums.TaskStatus;

import java.time.Instant;

public record TaskEvent(
        TaskEventType eventType,
        Long taskId,
        String title,
        String description,
        TaskStatus status,
        Long assignedToId,
        Long createdById,
        Instant timestamp
) {
}
