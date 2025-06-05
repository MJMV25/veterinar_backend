package com.example.clientservice.entity;

public enum ClientStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo"),
    SUSPENDED("Suspendido");

    private final String description;

    ClientStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}