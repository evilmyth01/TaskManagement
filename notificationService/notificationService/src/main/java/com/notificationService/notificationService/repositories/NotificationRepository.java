package com.notificationService.notificationService.repositories;

import com.notificationService.notificationService.entities.Notifications;
import com.notificationService.notificationService.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Long> {

    List<Notifications> findByUserId(Long userId);
    Page<Notifications> findByUserIdAndType(Long userId, NotificationType type, Pageable pageable);
    long countByUserIdAndReadFalse(Long userId);

}
