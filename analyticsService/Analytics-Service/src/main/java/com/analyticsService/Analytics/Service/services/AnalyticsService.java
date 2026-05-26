package com.analyticsService.Analytics.Service.services;

import com.analyticsService.Analytics.Service.dto.TaskEvent;
import com.analyticsService.Analytics.Service.entities.TaskEventLog;
import com.analyticsService.Analytics.Service.entities.TaskMetric;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


public interface AnalyticsService {

    public void processEvent(TaskEvent taskEvent);

    public TaskMetric getUserMetricData(Long userId);

    public List<TaskMetric> getTeamMetric();

    public List<TaskEventLog> getTaskHistory(Long taskId);

    public List<TaskEventLog> getUserActivity(Long userId, Instant from, Instant to);
}
