package com.analyticsService.Analytics.Service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAnalyticsResponse{

    private Long userId;
    private int totalCreated;
    private int totalAssigned;
    private int totalCompleted;
    private int inProgress;
    private Long avgCompletionMs;
    private String avfCompletionReadable;
    private Instant lastActivityAt;
}
