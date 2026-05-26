package com.notificationService.notificationService.services.impl;

import com.notificationService.notificationService.exceptions.ResourceNotFoundException;
import com.notificationService.notificationService.dtos.NotificationResponseDTO;
import com.notificationService.notificationService.entities.Notifications;
import com.notificationService.notificationService.repositories.NotificationRepository;
import com.notificationService.notificationService.services.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    NotificationRepository notificationRepository;
    ModelMapper modelMapper;

    @Override
    public List<NotificationResponseDTO> getAllNotificationForUser(Long userId, Pageable pageable) {

        List<Notifications> notifications = notificationRepository.findByUserId(userId);
        log.info("notification size "+notifications.toArray().length);
        return notifications
                .stream()
                .map(res -> modelMapper.map(res, NotificationResponseDTO.class))
                .collect(Collectors.toList());

    }

    @Override
    public Long unreadNotificationsCount(Long userId) {

        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    public NotificationResponseDTO getNotificationById(Long notificationId) {

        Notifications notification = notificationRepository.findById(notificationId)
                .orElseThrow(()-> new ResourceNotFoundException("No such notification find by this id "+ notificationId));

        return modelMapper.map(notification, NotificationResponseDTO.class);
    }

    @Override
    public NotificationResponseDTO updateNotificationStatus(Long notificationId) {

        Notifications notification = notificationRepository.findById(notificationId)
                .orElseThrow(()-> new ResourceNotFoundException("No such notification find by this id "+ notificationId));

        notification.setRead(true);
        Notifications updatedNotification = notificationRepository.save(notification);

        return modelMapper.map(updatedNotification, NotificationResponseDTO.class);
    }
}
