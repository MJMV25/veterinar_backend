package com.example.notificationservice.entity;


public enum NotificationStatus {
    PENDING("Pendiente"),
    SENT("Enviada"),
    DELIVERED("Entregada"),
    FAILED("Fallida"),
    CANCELLED("Cancelada"),
    READ("Leída");

    private final String description;

    NotificationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}