package com.example.billingservice.mapper;

import com.example.billingservice.dto.PaymentDto;
import com.example.billingservice.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDto toDto(Payment entity) {
        if (entity == null) {
            return null;
        }

        PaymentDto dto = new PaymentDto();
        dto.setId(entity.getId());
        dto.setInvoiceId(entity.getInvoice() != null ? entity.getInvoice().getId() : null);
        dto.setPaymentNumber(entity.getPaymentNumber());
        dto.setAmount(entity.getAmount());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setPaymentStatus(entity.getPaymentStatus());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setReferenceNumber(entity.getReferenceNumber());
        dto.setTransactionId(entity.getTransactionId());
        dto.setNotes(entity.getNotes());
        dto.setProcessedBy(entity.getProcessedBy());
        dto.setProcessedByName(entity.getProcessedByName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    public Payment toEntity(PaymentDto dto) {
        if (dto == null) {
            return null;
        }

        Payment entity = new Payment();
        entity.setId(dto.getId());
        entity.setPaymentNumber(dto.getPaymentNumber());
        entity.setAmount(dto.getAmount());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setPaymentStatus(dto.getPaymentStatus());
        entity.setPaymentDate(dto.getPaymentDate());
        entity.setReferenceNumber(dto.getReferenceNumber());
        entity.setTransactionId(dto.getTransactionId());
        entity.setNotes(dto.getNotes());
        entity.setProcessedBy(dto.getProcessedBy());
        entity.setProcessedByName(dto.getProcessedByName());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }
}