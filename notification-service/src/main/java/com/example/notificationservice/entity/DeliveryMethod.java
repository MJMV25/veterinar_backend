package com.example.notificationservice.entity;

public enum DeliveryMethod {
    EMAIL("Email"),
    SMS("SMS"),
    PUSH("Push Notification"),
    IN_APP("In-App Notification");

    private final String description;

    DeliveryMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}