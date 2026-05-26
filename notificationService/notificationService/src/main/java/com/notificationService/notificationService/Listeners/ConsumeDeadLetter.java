package com.notificationService.notificationService.Listeners;

import com.notificationService.notificationService.dtos.TaskEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;


@Slf4j
public class ConsumeDeadLetter {

    @KafkaListener(topics = "demo-topic-dlt")
    public void consumeDeadLetter(TaskEvent event) {

        log.error("Dead letter event received {}", event);
    }
}
