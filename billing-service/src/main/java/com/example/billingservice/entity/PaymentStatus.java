package com.example.billingservice.entity;

public enum PaymentStatus {
    PENDING("Pendiente"),
    PARTIAL("Pago Parcial"),
    PAID("Pagado"),
    REFUNDED("Reembolsado"),
    FAILED("Fallido");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
