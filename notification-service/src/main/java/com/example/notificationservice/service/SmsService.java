package com.example.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Value("${app.sms.enabled:false}")
    private boolean smsEnabled;

    @Value("${app.sms.api.url:}")
    private String smsApiUrl;

    @Value("${app.sms.api.key:}")
    private String smsApiKey;

    @Value("${app.sms.sender:VETERINARIA}")
    private String smsSender;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean sendSms(String phoneNumber, String message) {
        if (!smsEnabled) {
            logger.info("SMS sending is disabled. Would send SMS to: {} with message: {}", phoneNumber, message);
            return true; // Simular éxito cuando está deshabilitado
        }

        if (smsApiUrl == null || smsApiUrl.isEmpty()) {
            logger.warn("SMS API URL not configured. Cannot send SMS to: {}", phoneNumber);
            return false;
        }

        try {
            // Formatear número de teléfono (remover espacios y caracteres especiales)
            String formattedPhone = formatPhoneNumber(phoneNumber);

            // Preparar headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            if (smsApiKey != null && !smsApiKey.isEmpty()) {
                headers.set("Authorization", "Bearer " + smsApiKey);
            }

            // Preparar body del request (adaptable según proveedor SMS)
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("to", formattedPhone);
            requestBody.put("message", message);
            requestBody.put("from", smsSender);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Enviar SMS
            ResponseEntity<String> response = restTemplate.exchange(
                    smsApiUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("SMS sent successfully to: {}", formattedPhone);
                return true;
            } else {
                logger.error("Failed to send SMS to {}. Status: {}", formattedPhone, response.getStatusCode());
                return false;
            }

        } catch (Exception e) {
            logger.error("Error sending SMS to {}: ", phoneNumber, e);
            return false;
        }
    }

    public boolean sendBulkSms(Map<String, String> phoneToMessageMap) {
        boolean allSuccessful = true;

        for (Map.Entry<String, String> entry : phoneToMessageMap.entrySet()) {
            boolean sent = sendSms(entry.getKey(), entry.getValue());
            if (!sent) {
                allSuccessful = false;
            }
        }

        return allSuccessful;
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }

        // Remover espacios, guiones y paréntesis
        String formatted = phoneNumber.replaceAll("[\\s\\-\\(\\)]", "");

        // Si no empieza con +, agregar código de país (Colombia por defecto)
        if (!formatted.startsWith("+")) {
            if (formatted.startsWith("57")) {
                formatted = "+" + formatted;
            } else if (formatted.startsWith("3")) {
                formatted = "+57" + formatted;
            } else {
                formatted = "+57" + formatted;
            }
        }

        return formatted;
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        String formatted = formatPhoneNumber(phoneNumber);
        // Validación básica para números colombianos
        return formatted.matches("\\+57[3][0-9]{9}");
    }
}