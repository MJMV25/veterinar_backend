package com.example.inventoryservice.dto;

import com.example.inventoryservice.entity.MovementType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class StockMovementDto {
    private Long id;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Movement type is required")
    private MovementType movementType;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    private Integer previousStock;
    private Integer newStock;
    private String reason;
    private String referenceDocument;
    private Long userId;
    private LocalDateTime createdAt;

    // Product information
    private ProductInfo productInfo;

    // Constructors
    public StockMovementDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getPreviousStock() { return previousStock; }
    public void setPreviousStock(Integer previousStock) { this.previousStock = previousStock; }

    public Integer getNewStock() { return newStock; }
    public void setNewStock(Integer newStock) { this.newStock = newStock; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getReferenceDocument() { return referenceDocument; }
    public void setReferenceDocument(String referenceDocument) { this.referenceDocument = referenceDocument; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public ProductInfo getProductInfo() { return productInfo; }
    public void setProductInfo(ProductInfo productInfo) { this.productInfo = productInfo; }

    // Inner class for product information
    public static class ProductInfo {
        private Long id;
        private String productCode;
        private String productName;
        private String category;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }
}