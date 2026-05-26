package com.analyticsService.Analytics.Service.repositories;

import com.analyticsService.Analytics.Service.entities.TaskEventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TaskEventLogRepository extends JpaRepository<TaskEventLog, Long> {

    TaskEventLog findByUserId(Long userId);
    List<TaskEventLog> findByTaskId(Long taskId);
    TaskEventLog findByEventType(String eventType);
    List<TaskEventLog> findByUserIdAndOccurredAtBetween(Long userId, Instant from, Instant to);
}
