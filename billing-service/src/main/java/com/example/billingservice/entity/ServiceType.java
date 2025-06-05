package com.example.billingservice.entity;

public enum ServiceType {
    CONSULTATION("Consulta"),
    VACCINATION("Vacunación"),
    SURGERY("Cirugía"),
    EMERGENCY("Emergencia"),
    LABORATORY("Laboratorio"),
    IMAGING("Imagenología"),
    HOSPITALIZATION("Hospitalización"),
    GROOMING("Peluquería"),
    MEDICATION("Medicamento"),
    SUPPLIES("Insumos"),
    OTHER("Otro");

    private final String description;

    ServiceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}