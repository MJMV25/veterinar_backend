package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationDto;
import com.example.notificationservice.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NotificationService {
    NotificationDto createNotification(NotificationDto notificationDto);
    NotificationDto updateNotification(Long id, NotificationDto notificationDto);
    Optional<NotificationDto> getNotificationById(Long id);
    Page<NotificationDto> getAllNotifications(Pageable pageable);
    Page<NotificationDto> getNotificationsByRecipient(Long recipientId, Pageable pageable);
    Page<NotificationDto> getNotificationsByStatus(NotificationStatus status, Pageable pageable);
    Page<NotificationDto> getNotificationsByType(NotificationType type, Pageable pageable);
    Page<NotificationDto> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    void deleteNotification(Long id);

    // Sending methods
    void sendNotification(Long notificationId);
    void sendBulkNotification(List<Long> notificationIds);
    void scheduleNotification(NotificationDto notificationDto, LocalDateTime scheduledDate);

    // Template methods
    NotificationDto createFromTemplate(String templateName, Long recipientId, Map<String, Object> variables);

    // Processing methods
    void processPendingNotifications();
    void retryFailedNotifications();

    // Statistics
    Long getNotificationCountByStatus(NotificationStatus status);
    Map<NotificationStatus, Long> getNotificationStatistics();
}