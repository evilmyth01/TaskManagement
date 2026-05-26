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
@Table()
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TaskMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    Long userId;

    Integer totalCreated;

    Integer totalAssigned;

    Integer totalCompleted;

    Integer inProgress;

    Long    avgCompletionMs;

    Instant lastActivityAt;
}
