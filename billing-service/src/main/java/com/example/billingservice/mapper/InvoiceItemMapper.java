package com.example.billingservice.mapper;


import com.example.billingservice.dto.InvoiceItemDto;
import com.example.billingservice.entity.InvoiceItem;
import org.springframework.stereotype.Component;

@Component
public class InvoiceItemMapper {

    public InvoiceItemDto toDto(InvoiceItem entity) {
        if (entity == null) {
            return null;
        }

        InvoiceItemDto dto = new InvoiceItemDto();
        dto.setId(entity.getId());
        dto.setServiceType(entity.getServiceType());
        dto.setDescription(entity.getDescription());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setDiscountPercentage(entity.getDiscountPercentage());
        dto.setDiscountAmount(entity.getDiscountAmount());
        dto.setTotal(entity.getTotal());
        dto.setServiceId(entity.getServiceId());
        dto.setProductId(entity.getProductId());
        dto.setVeterinarianId(entity.getVeterinarianId());
        dto.setVeterinarianName(entity.getVeterinarianName());
        dto.setServiceDate(entity.getServiceDate());
        dto.setNotes(entity.getNotes());

        return dto;
    }

    public InvoiceItem toEntity(InvoiceItemDto dto) {
        if (dto == null) {
            return null;
        }

        InvoiceItem entity = new InvoiceItem();
        entity.setId(dto.getId());
        entity.setServiceType(dto.getServiceType());
        entity.setDescription(dto.getDescription());
        entity.setQuantity(dto.getQuantity());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setDiscountPercentage(dto.getDiscountPercentage());
        entity.setDiscountAmount(dto.getDiscountAmount());
        entity.setTotal(dto.getTotal());
        entity.setServiceId(dto.getServiceId());
        entity.setProductId(dto.getProductId());
        entity.setVeterinarianId(dto.getVeterinarianId());
        entity.setVeterinarianName(dto.getVeterinarianName());
        entity.setServiceDate(dto.getServiceDate());
        entity.setNotes(dto.getNotes());

        return entity;
    }
}
