package com.notificationService.notificationService.services;

import com.notificationService.notificationService.dtos.TaskEvent;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateBuilder {

    public String getMailSubject(TaskEvent taskEvent) {
        return switch (taskEvent.eventType()) {
            case TASK_CREATED -> createEmailSubjectForCreateEvent(taskEvent.title());
            case TASK_ASSIGNED -> createEmailSubjectForAssignedEvent(taskEvent.title());
            case TASK_STATUS_UPDATED -> createEmailSubjectForUpdateEvent(taskEvent.title());
        };

    }

    public String getMailBody(TaskEvent taskEvent) {
        return switch (taskEvent.eventType()) {
            case TASK_CREATED -> createEmailBodyForCreateEvent("","",taskEvent);
            case TASK_ASSIGNED -> createEmailBodyForAssignedEvent("","",taskEvent);
            case TASK_STATUS_UPDATED -> createEmailBodyForUpdateEvent("","",taskEvent);
        };

    }

    private String createEmailSubjectForCreateEvent(String title) {
        return "New Task Created: {{title}}";
    }

    private String createEmailBodyForCreateEvent(String assignedTo, String assignedBy, TaskEvent taskEvent) {
        return "Hello "+ assignedTo + ",\n" +
                "\n" +
                "A new task has been successfully created.\n" +
                "\n" +
                "Task Details:\n" +
                "- Title: {{taskEvent.title}}\n" +
                "- Description: {{taskEvent.description}}\n" +
                "\n" +
                "You can view and manage this task from your dashboard.\n" +
                "\n" +
                "Best regards,  \n" +
                "Vishal";
    }

    private String createEmailSubjectForUpdateEvent(String title) {
        return "Task Updated: {{title}}";
    }

    private String createEmailBodyForUpdateEvent(String assignedTo, String assignedBy, TaskEvent taskEvent) {
        return "Hello {{assignedTo}},\n" +
                "\n" +
                "The following task has been updated:\n" +
                "\n" +
                "Task Details:\n" +
                "- Title: {{taskTitle}}\n" +
                "- Description: {{taskEvent.description}}\n" +
                "\n" +
                "Please review the latest changes at your convenience.\n" +
                "\n" +
                "Best regards,  \n" +
                "Vishal ";
    }

    private String createEmailSubjectForAssignedEvent(String title) {
        return "New Task Assigned: {{title}}";
    }

    private String createEmailBodyForAssignedEvent(String assignedTo, String assignedBy, TaskEvent taskEvent) {
        return "Hello {{assignedTo}},\n" +
                "\n" +
                "You have been assigned a new task.\n" +
                "\n" +
                "Task Details:\n" +
                "- Title: {{taskTitle}}\n" +
                "- Description: {{taskDescription}}\n" +
                "- Assigned By: {{assignedBy}}\n" +
                "\n" +
                "Please start working on it and update the status as needed.\n" +
                "\n" +
                "Best regards,  \n" +
                "Vishal ";
    }
}
