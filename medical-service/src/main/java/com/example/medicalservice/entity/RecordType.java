package com.example.medicalservice.entity;

public enum RecordType {
    CONSULTA_GENERAL("Consulta General"),
    EMERGENCIA("Emergencia"),
    CIRUGIA("Cirugía"),
    VACUNACION("Vacunación"),
    REVISION("Revisión"),
    TRATAMIENTO("Tratamiento"),
    CONTROL("Control"),
    OTROS("Otros");

    private final String description;

    RecordType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}