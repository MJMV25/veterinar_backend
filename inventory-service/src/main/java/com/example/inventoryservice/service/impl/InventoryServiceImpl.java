package com.example.inventoryservice.service.impl;

import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.dto.StockMovementDto;
import com.example.inventoryservice.entity.*;
import com.example.userservice.exception.DuplicateResourceException;
import com.example.inventoryservice.exception.InsufficientStockException;
import com.example.inventoryservice.exception.ResourceNotFoundException;
import com.example.inventoryservice.mapper.ProductMapper;
import com.example.inventoryservice.mapper.StockMovementMapper;
import com.example.inventoryservice.repository.ProductRepository;
import com.example.inventoryservice.repository.StockMovementRepository;
import com.example.inventoryservice.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StockMovementMapper stockMovementMapper;

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        if (productRepository.existsByProductCode(productDto.getProductCode())) {
            throw new DuplicateResourceException("Product code already exists: " + productDto.getProductCode());
        }

        Product product = productMapper.toEntity(productDto);
        product.setStatus(ProductStatus.ACTIVE);

        Product savedProduct = productRepository.save(product);

        // Record initial stock movement if there's initial stock
        if (savedProduct.getCurrentStock() > 0) {
            recordStockMovement(savedProduct, MovementType.ENTRADA, savedProduct.getCurrentStock(),
                    0, savedProduct.getCurrentStock(), "Initial stock", null, null);
        }

        return productMapper.toDto(savedProduct);
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Check if product code is being changed and if it already exists
        if (!existingProduct.getProductCode().equals(productDto.getProductCode()) &&
                productRepository.existsByProductCode(productDto.getProductCode())) {
            throw new DuplicateResourceException("Product code already exists: " + productDto.getProductCode());
        }

        // Update fields (except current stock which should be managed through stock movements)
        existingProduct.setProductCode(productDto.getProductCode());
        existingProduct.setProductName(productDto.getProductName());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setCategory(productDto.getCategory());
        existingProduct.setMinimumStock(productDto.getMinimumStock());
        existingProduct.setMaximumStock(productDto.getMaximumStock());
        existingProduct.setUnitPrice(productDto.getUnitPrice());
        existingProduct.setCostPrice(productDto.getCostPrice());
        existingProduct.setUnitOfMeasure(productDto.getUnitOfMeasure());
        existingProduct.setBrand(productDto.getBrand());
        existingProduct.setSupplierName(productDto.getSupplierName());
        existingProduct.setExpirationDate(productDto.getExpirationDate());
        existingProduct.setBatchNumber(productDto.getBatchNumber());
        existingProduct.setReorderPoint(productDto.getReorderPoint());
        existingProduct.setLocation(productDto.getLocation());
        existingProduct.setNotes(productDto.getNotes());

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDto> getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDto> getProductByCode(String productCode) {
        return productRepository.findByProductCode(productCode)
                .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByStatus(ProductStatus status, Pageable pageable) {
        return productRepository.findByStatus(status, pageable)
                .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable)
                .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameOrCodeContaining(keyword, pageable)
                .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByBrand(String brand, Pageable pageable) {
        return productRepository.findByBrandContaining(brand, pageable)
                .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsBySupplier(String supplier, Pageable pageable) {
        return productRepository.findBySupplierContaining(supplier, pageable)
                .map(productMapper::toDto);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public void changeProductStatus(Long id, ProductStatus status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setStatus(status);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByProductCode(String productCode) {
        return productRepository.existsByProductCode(productCode);
    }

    @Override
    public void addStock(Long productId, Integer quantity, String reason, String referenceDocument, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Integer previousStock = product.getCurrentStock();
        Integer newStock = previousStock + quantity;

        product.setCurrentStock(newStock);
        productRepository.save(product);

        recordStockMovement(product, MovementType.ENTRADA, quantity, previousStock, newStock, reason, referenceDocument, userId);

        logger.info("Added {} units to product {}. Stock: {} -> {}", quantity, product.getProductCode(), previousStock, newStock);
    }

    @Override
    public void removeStock(Long productId, Integer quantity, String reason, String referenceDocument, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Integer previousStock = product.getCurrentStock();

        if (previousStock < quantity) {
            throw new InsufficientStockException("Insufficient stock. Available: " + previousStock + ", Requested: " + quantity);
        }

        Integer newStock = previousStock - quantity;

        product.setCurrentStock(newStock);
        productRepository.save(product);

        recordStockMovement(product, MovementType.SALIDA, quantity, previousStock, newStock, reason, referenceDocument, userId);

        logger.info("Removed {} units from product {}. Stock: {} -> {}", quantity, product.getProductCode(), previousStock, newStock);
    }

    @Override
    public void adjustStock(Long productId, Integer newQuantity, String reason, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Integer previousStock = product.getCurrentStock();
        Integer difference = newQuantity - previousStock;

        product.setCurrentStock(newQuantity);
        productRepository.save(product);

        MovementType movementType = difference >= 0 ? MovementType.AJUSTE_POSITIVO : MovementType.AJUSTE_NEGATIVO;

        recordStockMovement(product, movementType, Math.abs(difference), previousStock, newQuantity, reason, null, userId);

        logger.info("Adjusted stock for product {}. Stock: {} -> {}", product.getProductCode(), previousStock, newQuantity);
    }

    @Override
    public void transferStock(Long fromProductId, Long toProductId, Integer quantity, String reason, Long userId) {
        // Remove from source product
        removeStock(fromProductId, quantity, "Transfer out: " + reason, null, userId);

        // Add to destination product
        addStock(toProductId, quantity, "Transfer in: " + reason, null, userId);

        logger.info("Transferred {} units from product {} to product {}", quantity, fromProductId, toProductId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockMovementDto> getStockMovements(Pageable pageable) {
        return stockMovementRepository.findAll(pageable)
                .map(stockMovementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockMovementDto> getStockMovementsByProduct(Long productId, Pageable pageable) {
        return stockMovementRepository.findByProductId(productId, pageable)
                .map(stockMovementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockMovementDto> getStockMovementsByType(MovementType movementType, Pageable pageable) {
        return stockMovementRepository.findByMovementType(movementType, pageable)
                .map(stockMovementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockMovementDto> getStockMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return stockMovementRepository.findByDateRange(startDate, endDate, pageable)
                .map(stockMovementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementDto> getLatestMovementsByProduct(Long productId) {
        List<StockMovement> movements = stockMovementRepository.findLatestMovementsByProduct(productId);
        return movements.stream()
                .map(stockMovementMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getLowStockProducts() {
        List<Product> products = productRepository.findLowStockProducts();
        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getExpiredProducts() {
        List<Product> products = productRepository.findExpiredProducts(LocalDate.now());
        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsExpiringBetween(LocalDate startDate, LocalDate endDate) {
        List<Product> products = productRepository.findProductsExpiringBetween(startDate, endDate);
        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getOutOfStockProducts() {
        List<Product> products = productRepository.findOutOfStockProducts();
        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalInventoryValue() {
        return productRepository.calculateTotalInventoryValue();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getProductCountByStatus(ProductStatus status) {
        return productRepository.countByStatus(status);
    }

    private void recordStockMovement(Product product, MovementType movementType, Integer quantity,
                                     Integer previousStock, Integer newStock, String reason,
                                     String referenceDocument, Long userId) {
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setMovementType(movementType);
        movement.setQuantity(quantity);
        movement.setPreviousStock(previousStock);
        movement.setNewStock(newStock);
        movement.setReason(reason);
        movement.setReferenceDocument(referenceDocument);
        movement.setUserId(userId);

        stockMovementRepository.save(movement);
    }
}