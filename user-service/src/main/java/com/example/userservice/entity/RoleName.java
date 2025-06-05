package com.example.userservice.entity;

public enum RoleName {
    ADMIN("Administrador del sistema"),
    VETERINARIO("Veterinario"),
    RECEPCIONISTA("Recepcionista"),
    BODEGA("Encargado de bodega"),
    CLIENTE("Cliente del sistema");

    private final String description;

    RoleName(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}