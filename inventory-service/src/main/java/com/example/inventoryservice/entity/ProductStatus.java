package com.example.inventoryservice.entity;

public enum ProductStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo"),
    DISCONTINUED("Descontinuado"),
    OUT_OF_STOCK("Agotado");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}