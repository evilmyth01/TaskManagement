package com.notificationService.notificationService.Listeners;

import com.notificationService.notificationService.clients.AuthClient;
import com.notificationService.notificationService.dtos.TaskEvent;
import com.notificationService.notificationService.entities.Notifications;
import com.notificationService.notificationService.enums.NotificationStatus;
import com.notificationService.notificationService.enums.NotificationType;
import com.notificationService.notificationService.repositories.NotificationRepository;
import com.notificationService.notificationService.services.EmailService;
import com.notificationService.notificationService.services.EmailTemplateBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaConsumerService {

    NotificationRepository notificationRepository;
    EmailService emailService;
    EmailTemplateBuilder emailTemplateBuilder;
    AuthClient authClient;

    @KafkaListener(topics = "demo-topic", groupId = "notification-service")
    public void consume(TaskEvent taskEvent) {


        String email = "vishaltesting2024@gmail.com";
        String assignedTo = authClient.getUserEmail(taskEvent.assignedToId());
        String assignedBy = authClient.getUserEmail(taskEvent.createdById());
        String body = emailTemplateBuilder.getMailBody(taskEvent);
        String subject = emailTemplateBuilder.getMailSubject(taskEvent);

        Notifications notification = Notifications.builder()
                .userId(taskEvent.assignedToId())
                .email(email)
                .taskId(taskEvent.taskId())
                .body(body)
                .subject(subject)
                .type(NotificationType.valueOf(taskEvent.eventType().toString()))
                .build();

        notificationRepository.save(notification);

        try {
            emailService.send(assignedTo, assignedBy, subject, body);
            notification.setStatus(NotificationStatus.SENT);
            notification.setRead(false);

        } catch (Exception e) {

            log.info("Error while sending email "+e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            throw e;

        } finally {
            notificationRepository.save(notification);
        }

    }
}
