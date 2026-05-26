package com.analyticsService.Analytics.Service.services.impl;

import com.analyticsService.Analytics.Service.dto.TaskEvent;
import com.analyticsService.Analytics.Service.entities.TaskEventLog;
import com.analyticsService.Analytics.Service.entities.TaskMetric;
import com.analyticsService.Analytics.Service.repositories.TaskEventLogRepository;
import com.analyticsService.Analytics.Service.repositories.TaskMetricRepository;
import com.analyticsService.Analytics.Service.services.AnalyticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    // Have made some changes with orElseThrow which have to be changed to orElseGet and create the entity there.

    TaskMetricRepository taskMetricRepository;
    TaskEventLogRepository taskEventLogRepository;

    private static final String TASK_CREATED = "TASK_CREATED";
    private static final String TASK_ASSIGNED = "TASK_ASSIGNED";
    private static final String TASK_STATUS_UPDATED = "TASK_STATUS_UPDATED";
    private static final String DONE = "DONE";
    private static final String IN_PROGRESS = "IN_PROGRESS";

    public void processEvent(TaskEvent taskEvent) {

        saveTaskEventRecord(taskEvent);
        updateTaskMetricRecordOnBasisOfEventType(taskEvent);
    }

    public TaskMetric getUserMetricData(Long userId) {

        return taskMetricRepository.findByUserId(userId).orElseThrow();
    }

    public List<TaskMetric> getTeamMetric() {

        return taskMetricRepository.findAll();
    }

    public List<TaskEventLog> getTaskHistory(Long taskId) {

        return taskEventLogRepository.findByTaskId(taskId);
    }

    public List<TaskEventLog> getUserActivity(Long userId, Instant from, Instant to) {

        return taskEventLogRepository.findByUserIdAndOccurredAtBetween(userId, from, to);
    }


    private void saveTaskEventRecord(TaskEvent taskEvent) {

        TaskEventLog eventRecordToSave = TaskEventLog.builder()
                .taskId(taskEvent.taskId())
                .eventType(taskEvent.eventType())
                .userId(taskEvent.createdById())
                .occurredAt(taskEvent.timestamp())
                .receivedAt(Instant.now())
                .status(taskEvent.status())
                .build();

        taskEventLogRepository.save(eventRecordToSave);
    }

    private void updateTaskMetricRecordOnBasisOfEventType(TaskEvent taskEvent) {

        String taskEventType = taskEvent.eventType();

        switch (taskEventType) {

            case TASK_CREATED:
                updateTaskMetricForCreationEvent(taskEvent);
                break;
            case TASK_ASSIGNED:
                updateTaskMetricForAssignedEvent(taskEvent);
                break;
            case TASK_STATUS_UPDATED:
                updateTaskMetricForUpdateEvent(taskEvent);
            default:
                log.error("Unknown event");
        }
    }

    private void updateTaskMetricForCreationEvent(TaskEvent taskEvent) {

        Long createdBy = taskEvent.createdById();
        TaskMetric taskMetricForCreatedByUser = null;

        taskMetricForCreatedByUser = taskMetricRepository.findByUserId(createdBy)
                .orElseGet(() -> {
                    TaskMetric tm = TaskMetric.builder()
                            .userId(createdBy)
                            .totalCreated(0)
                            .totalAssigned(0)
                            .totalCompleted(0)
                            .inProgress(0)
                            .avgCompletionMs(0L)
                            .lastActivityAt(Instant.now())
                            .build();
                    return tm;
                });

            Integer totalCreated = taskMetricForCreatedByUser.getTotalCreated();
            taskMetricForCreatedByUser.setTotalCreated(totalCreated+1);

            taskMetricRepository.save(taskMetricForCreatedByUser);
    }

    private void updateTaskMetricForAssignedEvent(TaskEvent taskEvent) {

        Long assignedTo = taskEvent.assignedToId();
        TaskMetric taskMetricForAssignedToUser = null;

        taskMetricForAssignedToUser = taskMetricRepository.findByUserId(assignedTo).orElseThrow();
        if (taskMetricForAssignedToUser == null) {
            taskMetricForAssignedToUser = TaskMetric.builder()
                    .userId(assignedTo)
                    .totalCreated(0)
                    .totalAssigned(1)
                    .totalCompleted(0)
                    .inProgress(0)
                    .avgCompletionMs(0L)
                    .lastActivityAt(Instant.now())
                    .build();
        } else {
            Integer totalAssigned = taskMetricForAssignedToUser.getTotalAssigned();
            taskMetricForAssignedToUser.setTotalAssigned(totalAssigned+1);
        }

        taskMetricRepository.save(taskMetricForAssignedToUser);
    }

    private void updateTaskMetricForUpdateEvent(TaskEvent taskEvent) {

        Long assignedTo = taskEvent.assignedToId();
        TaskMetric taskMetricForAssignedToUser = null;

        //assuming that this will get triggered only after the assignment and creation event

        taskMetricForAssignedToUser = taskMetricRepository.findByUserId(assignedTo).orElseThrow();

        if (DONE.equals(taskEvent.status())) {

            updateTaskMetricForStatusDone(taskEvent, taskMetricForAssignedToUser);

        } else if (IN_PROGRESS.equals(taskEvent.status())) {

            int totalInProgress = taskMetricForAssignedToUser.getInProgress();
            taskMetricForAssignedToUser.setInProgress(totalInProgress+1);
        }


        taskMetricRepository.save(taskMetricForAssignedToUser);
    }

    private void updateTaskMetricForStatusDone(TaskEvent taskEvent, TaskMetric taskMetricForAssignedToUser) {
        int oldTotalCompleted = taskMetricForAssignedToUser.getTotalCompleted();
        long oldAvgCompletionTime = taskMetricForAssignedToUser.getAvgCompletionMs();

        taskMetricForAssignedToUser.setTotalCompleted(oldTotalCompleted+1);

        List<TaskEventLog> taskHistory = taskEventLogRepository.findByTaskId(taskEvent.taskId());
        Optional<TaskEventLog> taskEventLog = taskHistory.stream()
                .filter(th -> TASK_CREATED.equals(th.getEventType()))
                .findFirst();

        if (taskEventLog.isPresent()) {
            Instant startTime = taskEventLog.get().getOccurredAt();
            Instant endTime = taskEvent.timestamp();

            long completionTime = (endTime.toEpochMilli() - startTime.toEpochMilli());
            long newAvgCompletionTime = ((oldAvgCompletionTime * oldTotalCompleted) + completionTime) / (oldTotalCompleted+1);

            taskMetricForAssignedToUser.setAvgCompletionMs(newAvgCompletionTime);

        } else {
            log.error("No creation record found for this task id "+ taskEvent.taskId());
        }
    }
}
