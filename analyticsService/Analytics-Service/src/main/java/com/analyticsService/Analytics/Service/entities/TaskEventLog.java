package com.analyticsService.Analytics.Service.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        indexes = {
                @Index(name = "idx_user", columnList = "userId"),
                @Index(name = "idx_task", columnList = "taskId"),
                @Index(name = "idx_occurred", columnList = "occurredAt")
        }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TaskEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long taskId;

    @Column(nullable = false)
    String eventType;

    @Column(nullable = false)
    Long userId;

    @Column(nullable = false)
    Instant occurredAt;

    @Column(nullable = false)
    Instant receivedAt;

    @Column(nullable = false)
    String status;

}
