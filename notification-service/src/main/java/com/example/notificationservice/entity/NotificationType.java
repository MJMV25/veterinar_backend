package com.example.notificationservice.entity;

public enum NotificationType {
    APPOINTMENT_REMINDER("Recordatorio de Cita"),
    APPOINTMENT_CONFIRMATION("Confirmación de Cita"),
    APPOINTMENT_CANCELLATION("Cancelación de Cita"),
    VACCINATION_REMINDER("Recordatorio de Vacunación"),
    PAYMENT_DUE("Pago Pendiente"),
    PAYMENT_RECEIVED("Pago Recibido"),
    INVOICE_GENERATED("Factura Generada"),
    WELCOME("Bienvenida"),
    GENERAL("General"),
    EMERGENCY("Emergencia");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}