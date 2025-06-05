package com.example.inventoryservice.entity;

public enum ProductCategory {
    MEDICAMENTOS("Medicamentos"),
    VACUNAS("Vacunas"),
    ALIMENTOS("Alimentos"),
    SUPLEMENTOS("Suplementos"),
    EQUIPOS_MEDICOS("Equipos Médicos"),
    INSTRUMENTAL("Instrumental"),
    CONSUMIBLES("Consumibles"),
    LIMPIEZA("Productos de Limpieza"),
    ACCESORIOS("Accesorios"),
    OTROS("Otros");

    private final String description;

    ProductCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
