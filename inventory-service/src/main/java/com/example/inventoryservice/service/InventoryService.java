package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.dto.StockMovementDto;
import com.example.inventoryservice.entity.MovementType;
import com.example.inventoryservice.entity.ProductCategory;
import com.example.inventoryservice.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryService {
    // Product management
    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(Long id, ProductDto productDto);
    Optional<ProductDto> getProductById(Long id);
    Optional<ProductDto> getProductByCode(String productCode);
    Page<ProductDto> getAllProducts(Pageable pageable);
    Page<ProductDto> getProductsByStatus(ProductStatus status, Pageable pageable);
    Page<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable);
    Page<ProductDto> searchProducts(String keyword, Pageable pageable);
    Page<ProductDto> getProductsByBrand(String brand, Pageable pageable);
    Page<ProductDto> getProductsBySupplier(String supplier, Pageable pageable);
    void deleteProduct(Long id);
    void changeProductStatus(Long id, ProductStatus status);
    boolean existsByProductCode(String productCode);

    // Stock management
    void addStock(Long productId, Integer quantity, String reason, String referenceDocument, Long userId);
    void removeStock(Long productId, Integer quantity, String reason, String referenceDocument, Long userId);
    void adjustStock(Long productId, Integer newQuantity, String reason, Long userId);
    void transferStock(Long fromProductId, Long toProductId, Integer quantity, String reason, Long userId);

    // Stock movements
    Page<StockMovementDto> getStockMovements(Pageable pageable);
    Page<StockMovementDto> getStockMovementsByProduct(Long productId, Pageable pageable);
    Page<StockMovementDto> getStockMovementsByType(MovementType movementType, Pageable pageable);
    Page<StockMovementDto> getStockMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    List<StockMovementDto> getLatestMovementsByProduct(Long productId);

    // Alerts and reports
    List<ProductDto> getLowStockProducts();
    List<ProductDto> getExpiredProducts();
    List<ProductDto> getProductsExpiringBetween(LocalDate startDate, LocalDate endDate);
    List<ProductDto> getOutOfStockProducts();
    BigDecimal getTotalInventoryValue();
    Long getProductCountByStatus(ProductStatus status);
}
