package com.analyticsService.Analytics.Service.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskEventLogResponse{

    Long id;
    Long taskId;
    String eventType;
    Long userId;
    Instant occurredAt;
    Instant receivedAt;
    String status;
}
