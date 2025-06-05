package com.example.notificationservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.notificationservice.dto.NotificationDto;
import com.example.notificationservice.entity.*;
import com.example.notificationservice.exception.ResourceNotFoundException;
import com.example.notificationservice.mapper.NotificationMapper;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.EmailService;
import com.example.notificationservice.service.NotificationService;
import com.example.notificationservice.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public NotificationDto createNotification(NotificationDto notificationDto) {
        Notification notification = notificationMapper.toEntity(notificationDto);

        // Convert template variables to JSON string
        if (notificationDto.getTemplateVariables() != null) {
            try {
                String json = objectMapper.writeValueAsString(notificationDto.getTemplateVariables());
                notification.setTemplateVariables(json);
            } catch (JsonProcessingException e) {
                logger.error("Error converting template variables to JSON: ", e);
            }
        }

        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toDto(savedNotification);
    }

    @Override
    public NotificationDto updateNotification(Long id, NotificationDto notificationDto) {
        Notification existingNotification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        // Update fields
        existingNotification.setSubject(notificationDto.getSubject());
        existingNotification.setMessage(notificationDto.getMessage());
        existingNotification.setDeliveryMethod(notificationDto.getDeliveryMethod());
        existingNotification.setScheduledDate(notificationDto.getScheduledDate());
        existingNotification.setPriority(notificationDto.getPriority());

        Notification updatedNotification = notificationRepository.save(existingNotification);
        return notificationMapper.toDto(updatedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationDto> getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getAllNotifications(Pageable pageable) {
        return notificationRepository.findAll(pageable)
                .map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotificationsByRecipient(Long recipientId, Pageable pageable) {
        return notificationRepository.findByRecipientId(recipientId, pageable)
                .map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotificationsByStatus(NotificationStatus status, Pageable pageable) {
        return notificationRepository.findByStatus(status, pageable)
                .map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotificationsByType(NotificationType type, Pageable pageable) {
        return notificationRepository.findByNotificationType(type, pageable)
                .map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return notificationRepository.findByDateRange(startDate, endDate, pageable)
                .map(notificationMapper::toDto);
    }

    @Override
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
    }

    @Override
    @Async
    public void sendNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        try {
            boolean sent = false;

            switch (notification.getDeliveryMethod()) {
                case EMAIL:
                    sent = emailService.sendEmail(
                            notification.getRecipientEmail(),
                            notification.getSubject(),
                            notification.getMessage()
                    );
                    break;
                case SMS:
                    sent = smsService.sendSms(
                            notification.getRecipientPhone(),
                            notification.getMessage()
                    );
                    break;
                case PUSH:
                case IN_APP:
                    // Implement push notification logic here
                    sent = true; // Placeholder
                    break;
            }

            if (sent) {
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentDate(LocalDateTime.now());
                logger.info("Notification {} sent successfully", notificationId);
            } else {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setErrorMessage("Failed to send notification");
                logger.error("Failed to send notification {}", notificationId);
            }

            notification.setDeliveryAttempts(notification.getDeliveryAttempts() + 1);
            notificationRepository.save(notification);

        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notification.setDeliveryAttempts(notification.getDeliveryAttempts() + 1);
            notificationRepository.save(notification);
            logger.error("Error sending notification {}: ", notificationId, e);
        }
    }

    @Override
    @Async
    public void sendBulkNotification(List<Long> notificationIds) {
        for (Long notificationId : notificationIds) {
            sendNotification(notificationId);
        }
    }

    @Override
    public void scheduleNotification(NotificationDto notificationDto, LocalDateTime scheduledDate) {
        notificationDto.setScheduledDate(scheduledDate);
        notificationDto.setStatus(NotificationStatus.PENDING);
        createNotification(notificationDto);
    }

    @Override
    public NotificationDto createFromTemplate(String templateName, Long recipientId, Map<String, Object> variables) {
        NotificationDto notification = new NotificationDto();
        notification.setRecipientId(recipientId);
        notification.setTemplateName(templateName);
        notification.setTemplateVariables(variables);
        notification.setStatus(NotificationStatus.PENDING);

        // Set template-specific content
        setTemplateContent(notification, templateName, variables);

        return createNotification(notification);
    }

    @Override
    @Scheduled(fixedRate = 60000) // Every minute
    public void processPendingNotifications() {
        List<Notification> pendingNotifications = notificationRepository
                .findPendingNotifications(LocalDateTime.now());

        for (Notification notification : pendingNotifications) {
            sendNotification(notification.getId());
        }

        if (!pendingNotifications.isEmpty()) {
            logger.info("Processed {} pending notifications", pendingNotifications.size());
        }
    }

    @Override
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository
                .findFailedNotificationsForRetry();

        for (Notification notification : failedNotifications) {
            if (notification.getDeliveryAttempts() < 3) {
                sendNotification(notification.getId());
            }
        }

        if (!failedNotifications.isEmpty()) {
            logger.info("Retried {} failed notifications", failedNotifications.size());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNotificationCountByStatus(NotificationStatus status) {
        return notificationRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<NotificationStatus, Long> getNotificationStatistics() {
        Map<NotificationStatus, Long> statistics = new HashMap<>();
        for (NotificationStatus status : NotificationStatus.values()) {
            statistics.put(status, notificationRepository.countByStatus(status));
        }
        return statistics;
    }

    private void setTemplateContent(NotificationDto notification, String templateName, Map<String, Object> variables) {
        switch (templateName.toUpperCase()) {
            case "APPOINTMENT_REMINDER":
                notification.setNotificationType(NotificationType.APPOINTMENT_REMINDER);
                notification.setSubject("Recordatorio de Cita - Veterinaria");
                notification.setMessage(String.format(
                        "Estimado %s, le recordamos que tiene una cita programada para %s el %s a las %s. Por favor confirme su asistencia.",
                        variables.get("clientName"),
                        variables.get("petName"),
                        variables.get("appointmentDate"),
                        variables.get("appointmentTime")
                ));
                notification.setDeliveryMethod(DeliveryMethod.EMAIL);
                notification.setPriority(Priority.HIGH);
                break;

            case "VACCINATION_REMINDER":
                notification.setNotificationType(NotificationType.VACCINATION_REMINDER);
                notification.setSubject("Recordatorio de Vacunación - Veterinaria");
                notification.setMessage(String.format(
                        "Estimado %s, es hora de vacunar a %s. Por favor programe una cita lo antes posible.",
                        variables.get("clientName"),
                        variables.get("petName")
                ));
                notification.setDeliveryMethod(DeliveryMethod.EMAIL);
                notification.setPriority(Priority.HIGH);
                break;

            case "APPOINTMENT_CONFIRMATION":
                notification.setNotificationType(NotificationType.APPOINTMENT_CONFIRMATION);
                notification.setSubject("Confirmación de Cita - Veterinaria");
                notification.setMessage(String.format(
                        "Estimado %s, su cita para %s ha sido confirmada para el %s a las %s.",
                        variables.get("clientName"),
                        variables.get("petName"),
                        variables.get("appointmentDate"),
                        variables.get("appointmentTime")
                ));
                notification.setDeliveryMethod(DeliveryMethod.EMAIL);
                notification.setPriority(Priority.NORMAL);
                break;

            case "APPOINTMENT_CANCELLATION":
                notification.setNotificationType(NotificationType.APPOINTMENT_CANCELLATION);
                notification.setSubject("Cancelación de Cita - Veterinaria");
                notification.setMessage(String.format(
                        "Estimado %s, su cita para %s programada para el %s a las %s ha sido cancelada. Por favor contacte con nosotros para reprogramar.",
                        variables.get("clientName"),
                        variables.get("petName"),
                        variables.get("appointmentDate"),
                        variables.get("appointmentTime")
                ));
                notification.setDeliveryMethod(DeliveryMethod.EMAIL);
                notification.setPriority(Priority.HIGH);
                break;

            case "PAYMENT_DUE":
                notification.setNotificationType(NotificationType.PAYMENT_DUE);
                notification.setSubject("Pago Pendiente - Veterinaria");
                notification.setMessage(String.format(
                        "Estimado %s, tiene un pago pendiente de $%s por los servicios prestados. Factura #%s.",
                        variables.get("clientName"),
                        variables.get("amount"),
                        variables.get("invoiceNumber")
                ));
                notification.setDeliveryMethod(DeliveryMethod.EMAIL);
                notification.setPriority(Priority.HIGH);
                break;

            case "PAYMENT_RECEIVED":
                notification.setNotificationType(NotificationType.PAYMENT_RECEIVED);
                notification.setSubject("Pago Recibido - Veterinaria");
                notification.setMessage(String.format(
                        "Estimado %s, hemos recibido su pago de $%s. Gracias por su confianza. Factura #%s.",
                        variables.get("clientName"),
                        variables.get("amount"),
                        variables.get("invoiceNumber")
                ));
                notification.setDeliveryMethod(DeliveryMethod.EMAIL);
                notification.setPriority(Priority.NORMAL);
                break;

            case "INVOICE_GENERATED":
                notification.setNotificationType(NotificationType.INVOICE_GENERATED);
                notification.setSubject("Nueva Factura - Veterinaria");
                notification.setMessage(String.format(
                        "Estimado %s, se ha generado una nueva factura #%s por $%s. Puede descargarla desde nuestro portal.",
                        variables.get("clientName"),
                        variables.get("invoiceNumber"),
                        variables.get("amount")
                ));
                notification.setDeliveryMethod(DeliveryMethod.EMAIL);
                notification.setPriority(Priority.NORMAL);
                break;

            case "WELCOME":
                notification.setNotificationType(NotificationType.WELCOME);
                notification.setSubject("Bienvenido a Veterinaria");
                notification.setMessage(String.format(
                        "Estimado %s, bienvenido a nuestra veterinaria. Gracias por confiar en nosotros para el cuidado de %s.",
                        variables.get("clientName"),
                        variables.get("petName")
                ));
                notification.setDeliveryMethod(DeliveryMethod.EMAIL);
                notification.setPriority(Priority.NORMAL);
                break;

            case "EMERGENCY":
                notification.setNotificationType(NotificationType.EMERGENCY);
                notification.setSubject("EMERGENCIA - Veterinaria");
                notification.setMessage(String.format(
                        "URGENTE: Estimado %s, necesitamos que traiga a %s inmediatamente. Motivo: %s. Contacte con nosotros: %s",
                        variables.get("clientName"),
                        variables.get("petName"),
                        variables.getOrDefault("reason", "Consulte con el veterinario"),
                        variables.getOrDefault("emergencyPhone", "(123) 456-7890")
                ));
                notification.setDeliveryMethod(DeliveryMethod.SMS);
                notification.setPriority(Priority.URGENT);
                break;

            default:
                notification.setNotificationType(NotificationType.GENERAL);
                notification.setSubject("Notificación - Veterinaria");
                notification.setMessage("Tiene una nueva notificación de la veterinaria.");
                notification.setDeliveryMethod(DeliveryMethod.EMAIL);
                notification.setPriority(Priority.NORMAL);
                break;
        }
    }
}