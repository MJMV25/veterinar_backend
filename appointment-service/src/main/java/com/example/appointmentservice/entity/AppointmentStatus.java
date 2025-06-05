package com.example.appointmentservice.entity;

public enum AppointmentStatus {
    SCHEDULED("Programada"),
    CONFIRMED("Confirmada"),
    IN_PROGRESS("En Progreso"),
    COMPLETED("Completada"),
    CANCELLED("Cancelada"),
    NO_SHOW("No Asistió");

    private final String description;

    AppointmentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}