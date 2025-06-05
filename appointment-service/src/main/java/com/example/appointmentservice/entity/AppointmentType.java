package com.example.appointmentservice.entity;


public enum AppointmentType {
    CONSULTA_GENERAL("Consulta General"),
    VACUNACION("Vacunación"),
    DESPARASITACION("Desparasitación"),
    CIRUGIA("Cirugía"),
    EMERGENCIA("Emergencia"),
    REVISION("Revisión"),
    ESTERILIZACION("Esterilización"),
    TRATAMIENTO("Tratamiento"),
    OTROS("Otros");

    private final String description;

    AppointmentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}