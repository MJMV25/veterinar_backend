package com.example.billingservice.exception;

public class InvoiceNotFoundException extends BillingException {

    public InvoiceNotFoundException(String message) {
        super(message);
    }

    public InvoiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
