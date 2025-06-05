package com.example.billingservice.entity;

public enum PaymentMethod {
    CASH("Efectivo"),
    CREDIT_CARD("Tarjeta de Crédito"),
    DEBIT_CARD("Tarjeta Débito"),
    BANK_TRANSFER("Transferencia Bancaria"),
    CHECK("Cheque"),
    DIGITAL_WALLET("Billetera Digital"),
    OTHER("Otro");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}