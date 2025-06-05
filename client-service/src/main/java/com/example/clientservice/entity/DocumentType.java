package com.example.clientservice.entity;

public enum DocumentType {
    CEDULA("Cédula de Ciudadanía"),
    PASAPORTE("Pasaporte"),
    CEDULA_EXTRANJERIA("Cédula de Extranjería"),
    NIT("NIT");

    private final String description;

    DocumentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
