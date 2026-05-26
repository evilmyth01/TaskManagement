package com.analyticsService.Analytics.Service.controllers;

import com.analyticsService.Analytics.Service.dto.TaskEventLogResponse;
import com.analyticsService.Analytics.Service.dto.UserAnalyticsResponse;
import com.analyticsService.Analytics.Service.services.AnalyticsService;
import com.analyticsService.Analytics.Service.services.impl.AnalyticsServiceImpl;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping("/analytics")
public class AnalyticsController {

    AnalyticsService analyticsService;
    ModelMapper modelMapper;

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserAnalyticsResponse> getAnalyticsByUserId(@PathVariable @NotNull Long userId) {

        return ResponseEntity.ok(modelMapper.map(analyticsService.getUserMetricData(userId), UserAnalyticsResponse.class));
    }

    @GetMapping("/user/{userId}/activity")
    public ResponseEntity<List<TaskEventLogResponse>> getUserActivitiesForAPeriod(
            @PathVariable @NotNull Long userId,
            @RequestParam(name = "from")Instant from,
            @RequestParam(name = "to")Instant to
            ) {

        List<TaskEventLogResponse> userActivities = analyticsService.getUserActivity(userId, from, to)
                .stream()
                .map(tr -> modelMapper.map(tr, TaskEventLogResponse.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userActivities);
    }
}
