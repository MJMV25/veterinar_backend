package com.example.inventoryservice.mapper;

import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }

        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setProductCode(product.getProductCode());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory());
        dto.setCurrentStock(product.getCurrentStock());
        dto.setMinimumStock(product.getMinimumStock());
        dto.setMaximumStock(product.getMaximumStock());
        dto.setUnitPrice(product.getUnitPrice());
        dto.setCostPrice(product.getCostPrice());
        dto.setUnitOfMeasure(product.getUnitOfMeasure());
        dto.setBrand(product.getBrand());
        dto.setSupplierName(product.getSupplierName());
        dto.setExpirationDate(product.getExpirationDate());
        dto.setBatchNumber(product.getBatchNumber());
        dto.setStatus(product.getStatus());
        dto.setReorderPoint(product.getReorderPoint());
        dto.setLocation(product.getLocation());
        dto.setNotes(product.getNotes());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        // Set calculated fields
        dto.setLowStock(product.isLowStock());
        dto.setExpired(product.isExpired());
        dto.setExpiringSoon(product.isExpiringSoon(30)); // 30 days

        return dto;
    }

    public Product toEntity(ProductDto dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setId(dto.getId());
        product.setProductCode(dto.getProductCode());
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setCategory(dto.getCategory());
        product.setCurrentStock(dto.getCurrentStock());
        product.setMinimumStock(dto.getMinimumStock());
        product.setMaximumStock(dto.getMaximumStock());
        product.setUnitPrice(dto.getUnitPrice());
        product.setCostPrice(dto.getCostPrice());
        product.setUnitOfMeasure(dto.getUnitOfMeasure());
        product.setBrand(dto.getBrand());
        product.setSupplierName(dto.getSupplierName());
        product.setExpirationDate(dto.getExpirationDate());
        product.setBatchNumber(dto.getBatchNumber());
        product.setStatus(dto.getStatus());
        product.setReorderPoint(dto.getReorderPoint());
        product.setLocation(dto.getLocation());
        product.setNotes(dto.getNotes());

        return product;
    }
}