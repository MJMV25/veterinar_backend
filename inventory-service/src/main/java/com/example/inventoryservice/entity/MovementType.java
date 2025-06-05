package com.example.inventoryservice.entity;

public enum MovementType {
    ENTRADA("Entrada"),
    SALIDA("Salida"),
    AJUSTE_POSITIVO("Ajuste Positivo"),
    AJUSTE_NEGATIVO("Ajuste Negativo"),
    DEVOLUCION("Devolución"),
    PERDIDA("Pérdida"),
    VENCIMIENTO("Vencimiento");

    private final String description;

    MovementType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}