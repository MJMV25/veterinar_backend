package com.example.billingservice.mapper;

import com.example.billingservice.dto.InvoiceDto;
import com.example.billingservice.entity.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class InvoiceMapper {

    @Autowired
    private InvoiceItemMapper invoiceItemMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    public InvoiceDto toDto(Invoice entity) {
        if (entity == null) {
            return null;
        }

        InvoiceDto dto = new InvoiceDto();
        dto.setId(entity.getId());
        dto.setInvoiceNumber(entity.getInvoiceNumber());
        dto.setClientId(entity.getClientId());
        dto.setClientName(entity.getClientName());
        dto.setClientEmail(entity.getClientEmail());
        dto.setClientPhone(entity.getClientPhone());
        dto.setClientAddress(entity.getClientAddress());
        dto.setPetId(entity.getPetId());
        dto.setPetName(entity.getPetName());
        dto.setAppointmentId(entity.getAppointmentId());
        dto.setStatus(entity.getStatus());
        dto.setPaymentStatus(entity.getPaymentStatus());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setSubtotal(entity.getSubtotal());
        dto.setTaxAmount(entity.getTaxAmount());
        dto.setDiscountAmount(entity.getDiscountAmount());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setPaidAmount(entity.getPaidAmount());
        dto.setBalanceDue(entity.getBalanceDue());
        dto.setTaxPercentage(entity.getTaxPercentage());
        dto.setDiscountPercentage(entity.getDiscountPercentage());
        dto.setNotes(entity.getNotes());
        dto.setTermsConditions(entity.getTermsConditions());
        dto.setIssueDate(entity.getIssueDate());
        dto.setDueDate(entity.getDueDate());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedBy(entity.getCreatedBy());

        // Map items if present
        if (entity.getItems() != null) {
            dto.setItems(entity.getItems().stream()
                    .map(invoiceItemMapper::toDto)
                    .collect(Collectors.toList()));
        }

        // Map payments if present
        if (entity.getPayments() != null) {
            dto.setPayments(entity.getPayments().stream()
                    .map(paymentMapper::toDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Invoice toEntity(InvoiceDto dto) {
        if (dto == null) {
            return null;
        }

        Invoice entity = new Invoice();
        entity.setId(dto.getId());
        entity.setInvoiceNumber(dto.getInvoiceNumber());
        entity.setClientId(dto.getClientId());
        entity.setClientName(dto.getClientName());
        entity.setClientEmail(dto.getClientEmail());
        entity.setClientPhone(dto.getClientPhone());
        entity.setClientAddress(dto.getClientAddress());
        entity.setPetId(dto.getPetId());
        entity.setPetName(dto.getPetName());
        entity.setAppointmentId(dto.getAppointmentId());
        entity.setStatus(dto.getStatus());
        entity.setPaymentStatus(dto.getPaymentStatus());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setSubtotal(dto.getSubtotal());
        entity.setTaxAmount(dto.getTaxAmount());
        entity.setDiscountAmount(dto.getDiscountAmount());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setPaidAmount(dto.getPaidAmount());
        entity.setBalanceDue(dto.getBalanceDue());
        entity.setTaxPercentage(dto.getTaxPercentage());
        entity.setDiscountPercentage(dto.getDiscountPercentage());
        entity.setNotes(dto.getNotes());
        entity.setTermsConditions(dto.getTermsConditions());
        entity.setIssueDate(dto.getIssueDate());
        entity.setDueDate(dto.getDueDate());
        entity.setPaymentDate(dto.getPaymentDate());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setCreatedBy(dto.getCreatedBy());

        return entity;
    }
}