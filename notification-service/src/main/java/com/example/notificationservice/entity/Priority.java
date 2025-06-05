package com.example.notificationservice.entity;

public enum Priority {
    LOW("Baja"),
    NORMAL("Normal"),
    HIGH("Alta"),
    URGENT("Urgente");

    private final String description;

    Priority(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}