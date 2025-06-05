package com.example.notificationservice.entity;

public enum RecipientType {
    CLIENT("Cliente"),
    USER("Usuario"),
    VETERINARIAN("Veterinario"),
    ADMIN("Administrador");

    private final String description;

    RecipientType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
