package com.example.inventoryservice.mapper;

import com.example.inventoryservice.dto.StockMovementDto;
import com.example.inventoryservice.entity.StockMovement;
import org.springframework.stereotype.Component;

@Component
public class StockMovementMapper {

    public StockMovementDto toDto(StockMovement stockMovement) {
        if (stockMovement == null) {
            return null;
        }

        StockMovementDto dto = new StockMovementDto();
        dto.setId(stockMovement.getId());
        dto.setProductId(stockMovement.getProduct().getId());
        dto.setMovementType(stockMovement.getMovementType());
        dto.setQuantity(stockMovement.getQuantity());
        dto.setPreviousStock(stockMovement.getPreviousStock());
        dto.setNewStock(stockMovement.getNewStock());
        dto.setReason(stockMovement.getReason());
        dto.setReferenceDocument(stockMovement.getReferenceDocument());
        dto.setUserId(stockMovement.getUserId());
        dto.setCreatedAt(stockMovement.getCreatedAt());

        // Set product information
        if (stockMovement.getProduct() != null) {
            StockMovementDto.ProductInfo productInfo = new StockMovementDto.ProductInfo();
            productInfo.setId(stockMovement.getProduct().getId());
            productInfo.setProductCode(stockMovement.getProduct().getProductCode());
            productInfo.setProductName(stockMovement.getProduct().getProductName());
            productInfo.setCategory(stockMovement.getProduct().getCategory() != null ?
                    stockMovement.getProduct().getCategory().name() : null);
            dto.setProductInfo(productInfo);
        }

        return dto;
    }

    public StockMovement toEntity(StockMovementDto dto) {
        if (dto == null) {
            return null;
        }

        StockMovement stockMovement = new StockMovement();
        stockMovement.setId(dto.getId());
        stockMovement.setMovementType(dto.getMovementType());
        stockMovement.setQuantity(dto.getQuantity());
        stockMovement.setPreviousStock(dto.getPreviousStock());
        stockMovement.setNewStock(dto.getNewStock());
        stockMovement.setReason(dto.getReason());
        stockMovement.setReferenceDocument(dto.getReferenceDocument());
        stockMovement.setUserId(dto.getUserId());

        return stockMovement;
    }
}