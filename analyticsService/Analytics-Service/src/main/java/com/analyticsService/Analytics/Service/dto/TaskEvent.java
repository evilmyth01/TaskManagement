package com.analyticsService.Analytics.Service.dto;

import java.time.Instant;

public record TaskEvent(
        String  eventType,
        Long taskId,
        String title,
        String description,
        String status,
        Long assignedToId,
        Long createdById,
        Instant timestamp
) {
}
