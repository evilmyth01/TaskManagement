package com.analyticsService.Analytics.Service.Listeners;

import com.analyticsService.Analytics.Service.dto.TaskEvent;
import com.analyticsService.Analytics.Service.services.AnalyticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KafkaConsumer {

    AnalyticsService analyticsService;

    @KafkaListener(topics = "demo-topic", groupId = "analytics-service")
    public void consume(TaskEvent taskEvent) {

        // add some checks on the taskevent for task id and such info. also update the final activity part also.
        log.info("received kafka message in analytics service" + taskEvent);
        analyticsService.processEvent(taskEvent);
    }
}
