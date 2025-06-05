package com.example.billingservice.entity;

public enum InvoiceStatus {
    PENDING("Pendiente"),
    DRAFT("Borrador"),
    SENT("Enviada"),
    PAID("Pagada"),
    OVERDUE("Vencida"),
    CANCELLED("Cancelada");

    private final String description;

    InvoiceStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}