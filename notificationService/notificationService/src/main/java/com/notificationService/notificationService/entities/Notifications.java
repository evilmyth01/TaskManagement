package com.notificationService.notificationService.entities;

import com.notificationService.notificationService.enums.NotificationStatus;
import com.notificationService.notificationService.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_user_id", columnList = "userId")
})
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = true)
    Long userId;

    @Column(nullable = false)
    String email;

    @Column(nullable = false)
    String subject;

    @Column(nullable = false)
    String body;

    @Enumerated(value = EnumType.STRING)
    NotificationType type;

    @Enumerated(value = EnumType.STRING)
    NotificationStatus status;

    @Column(nullable = false)
    Long taskId;

    @CreationTimestamp
    Instant createdAt;

    @Column()
    Boolean read = false;

}
