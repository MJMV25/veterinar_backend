package com.example.petservice.entity;

public enum PetStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo"),
    DECEASED("Fallecido");

    private final String description;

    PetStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}