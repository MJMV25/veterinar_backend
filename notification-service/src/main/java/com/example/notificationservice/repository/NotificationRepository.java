package com.example.notificationservice.repository;

import com.example.notificationservice.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByRecipientId(Long recipientId, Pageable pageable);
    Page<Notification> findByStatus(NotificationStatus status, Pageable pageable);
    Page<Notification> findByNotificationType(NotificationType notificationType, Pageable pageable);
    Page<Notification> findByDeliveryMethod(DeliveryMethod deliveryMethod, Pageable pageable);
    Page<Notification> findByPriority(Priority priority, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.scheduledDate <= :currentTime AND n.status = 'PENDING'")
    List<Notification> findPendingNotifications(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.deliveryAttempts < 3")
    List<Notification> findFailedNotificationsForRetry();

    @Query("SELECT n FROM Notification n WHERE n.createdAt BETWEEN :startDate AND :endDate")
    Page<Notification> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.status = :status")
    Long countByStatus(@Param("status") NotificationStatus status);

    @Query("SELECT n FROM Notification n WHERE n.referenceId = :referenceId AND n.referenceType = :referenceType")
    List<Notification> findByReference(@Param("referenceId") Long referenceId, @Param("referenceType") String referenceType);
}