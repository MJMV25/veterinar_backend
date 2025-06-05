package com.example.notificationservice.controller;


import com.example.notificationservice.dto.NotificationDto;
import com.example.notificationservice.entity.NotificationStatus;
import com.example.notificationservice.entity.NotificationType;
import com.example.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(@Valid @RequestBody NotificationDto notificationDto) {
        NotificationDto createdNotification = notificationService.createNotification(notificationDto);
        return new ResponseEntity<>(createdNotification, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationDto> updateNotification(
            @PathVariable Long id,
            @Valid @RequestBody NotificationDto notificationDto) {
        NotificationDto updatedNotification = notificationService.updateNotification(id, notificationDto);
        return ResponseEntity.ok(updatedNotification);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDto> getNotificationById(@PathVariable Long id) {
        Optional<NotificationDto> notification = notificationService.getNotificationById(id);
        return notification.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<NotificationDto>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<NotificationDto> notifications = notificationService.getAllNotifications(pageable);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<Page<NotificationDto>> getNotificationsByRecipient(
            @PathVariable Long recipientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NotificationDto> notifications = notificationService.getNotificationsByRecipient(recipientId, pageable);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<NotificationDto>> getNotificationsByStatus(
            @PathVariable NotificationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NotificationDto> notifications = notificationService.getNotificationsByStatus(status, pageable);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Page<NotificationDto>> getNotificationsByType(
            @PathVariable NotificationType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NotificationDto> notifications = notificationService.getNotificationsByType(type, pageable);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<NotificationDto>> getNotificationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NotificationDto> notifications = notificationService.getNotificationsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<String> sendNotification(@PathVariable Long id) {
        notificationService.sendNotification(id);
        return ResponseEntity.ok("Notification sent successfully");
    }

    @PostMapping("/send-bulk")
    public ResponseEntity<String> sendBulkNotification(@RequestBody List<Long> notificationIds) {
        notificationService.sendBulkNotification(notificationIds);
        return ResponseEntity.ok("Bulk notifications sent successfully");
    }

    @PostMapping("/schedule")
    public ResponseEntity<NotificationDto> scheduleNotification(
            @Valid @RequestBody NotificationDto notificationDto,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledDate) {

        notificationService.scheduleNotification(notificationDto, scheduledDate);
        return new ResponseEntity<>(notificationDto, HttpStatus.CREATED);
    }

    @PostMapping("/template/{templateName}")
    public ResponseEntity<NotificationDto> createFromTemplate(
            @PathVariable String templateName,
            @RequestParam Long recipientId,
            @RequestBody Map<String, Object> variables) {

        NotificationDto notification = notificationService.createFromTemplate(templateName, recipientId, variables);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

    @PostMapping("/process-pending")
    public ResponseEntity<String> processPendingNotifications() {
        notificationService.processPendingNotifications();
        return ResponseEntity.ok("Pending notifications processed");
    }

    @PostMapping("/retry-failed")
    public ResponseEntity<String> retryFailedNotifications() {
        notificationService.retryFailedNotifications();
        return ResponseEntity.ok("Failed notifications retried");
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<NotificationStatus, Long>> getNotificationStatistics() {
        Map<NotificationStatus, Long> statistics = notificationService.getNotificationStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/count/{status}")
    public ResponseEntity<Long> getNotificationCountByStatus(@PathVariable NotificationStatus status) {
        Long count = notificationService.getNotificationCountByStatus(status);
        return ResponseEntity.ok(count);
    }
}