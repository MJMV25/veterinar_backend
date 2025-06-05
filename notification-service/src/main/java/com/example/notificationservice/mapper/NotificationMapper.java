package com.example.notificationservice.mapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.notificationservice.dto.NotificationDto;
import com.example.notificationservice.entity.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationMapper {

    private static final Logger logger = LoggerFactory.getLogger(NotificationMapper.class);

    @Autowired
    private ObjectMapper objectMapper;

    public NotificationDto toDto(Notification entity) {
        if (entity == null) {
            return null;
        }

        NotificationDto dto = new NotificationDto();
        dto.setId(entity.getId());
        dto.setRecipientId(entity.getRecipientId());
        dto.setRecipientType(entity.getRecipientType());
        dto.setNotificationType(entity.getNotificationType());
        dto.setDeliveryMethod(entity.getDeliveryMethod());
        dto.setSubject(entity.getSubject());
        dto.setMessage(entity.getMessage());
        dto.setRecipientEmail(entity.getRecipientEmail());
        dto.setRecipientPhone(entity.getRecipientPhone());
        dto.setStatus(entity.getStatus());
        dto.setScheduledDate(entity.getScheduledDate());
        dto.setSentDate(entity.getSentDate());
        dto.setDeliveryAttempts(entity.getDeliveryAttempts());
        dto.setErrorMessage(entity.getErrorMessage());
        dto.setReferenceId(entity.getReferenceId());
        dto.setReferenceType(entity.getReferenceType());
        dto.setTemplateName(entity.getTemplateName());
        dto.setPriority(entity.getPriority());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Convert JSON string to Map for template variables
        if (entity.getTemplateVariables() != null && !entity.getTemplateVariables().isEmpty()) {
            try {
                Map<String, Object> variables = objectMapper.readValue(
                        entity.getTemplateVariables(),
                        new TypeReference<Map<String, Object>>() {}
                );
                dto.setTemplateVariables(variables);
            } catch (JsonProcessingException e) {
                logger.error("Error parsing template variables JSON: ", e);
                dto.setTemplateVariables(new HashMap<>());
            }
        }

        return dto;
    }

    public Notification toEntity(NotificationDto dto) {
        if (dto == null) {
            return null;
        }

        Notification entity = new Notification();
        entity.setId(dto.getId());
        entity.setRecipientId(dto.getRecipientId());
        entity.setRecipientType(dto.getRecipientType());
        entity.setNotificationType(dto.getNotificationType());
        entity.setDeliveryMethod(dto.getDeliveryMethod());
        entity.setSubject(dto.getSubject());
        entity.setMessage(dto.getMessage());
        entity.setRecipientEmail(dto.getRecipientEmail());
        entity.setRecipientPhone(dto.getRecipientPhone());
        entity.setStatus(dto.getStatus());
        entity.setScheduledDate(dto.getScheduledDate());
        entity.setSentDate(dto.getSentDate());
        entity.setDeliveryAttempts(dto.getDeliveryAttempts());
        entity.setErrorMessage(dto.getErrorMessage());
        entity.setReferenceId(dto.getReferenceId());
        entity.setReferenceType(dto.getReferenceType());
        entity.setTemplateName(dto.getTemplateName());
        entity.setPriority(dto.getPriority());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }
}
