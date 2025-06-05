package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.dto.StockMovementDto;
import com.example.inventoryservice.entity.MovementType;
import com.example.inventoryservice.entity.ProductCategory;
import com.example.inventoryservice.entity.ProductStatus;
import com.example.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // Product endpoints
    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        ProductDto createdProduct = inventoryService.createProduct(productDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return inventoryService.getProductById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/products/code/{productCode}")
    public ResponseEntity<ProductDto> getProductByCode(@PathVariable String productCode) {
        return inventoryService.getProductByCode(productCode)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/products")
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductDto> products = inventoryService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/status/{status}")
    public ResponseEntity<Page<ProductDto>> getProductsByStatus(
            @PathVariable ProductStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = inventoryService.getProductsByStatus(status, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/category/{category}")
    public ResponseEntity<Page<ProductDto>> getProductsByCategory(
            @PathVariable ProductCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = inventoryService.getProductsByCategory(category, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = inventoryService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/brand/{brand}")
    public ResponseEntity<Page<ProductDto>> getProductsByBrand(
            @PathVariable String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = inventoryService.getProductsByBrand(brand, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/supplier/{supplier}")
    public ResponseEntity<Page<ProductDto>> getProductsBySupplier(
            @PathVariable String supplier,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDto> products = inventoryService.getProductsBySupplier(supplier, pageable);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDto) {
        ProductDto updatedProduct = inventoryService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/products/{id}/status")
    public ResponseEntity<Void> changeProductStatus(@PathVariable Long id, @RequestParam ProductStatus status) {
        inventoryService.changeProductStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        inventoryService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products/exists/{productCode}")
    public ResponseEntity<Boolean> existsByProductCode(@PathVariable String productCode) {
        boolean exists = inventoryService.existsByProductCode(productCode);
        return ResponseEntity.ok(exists);
    }

    // Stock management endpoints
    @PostMapping("/products/{productId}/stock/add")
    public ResponseEntity<Void> addStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity,
            @RequestParam String reason,
            @RequestParam(required = false) String referenceDocument,
            @RequestParam(required = false) Long userId) {

        inventoryService.addStock(productId, quantity, reason, referenceDocument, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/products/{productId}/stock/remove")
    public ResponseEntity<Void> removeStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity,
            @RequestParam String reason,
            @RequestParam(required = false) String referenceDocument,
            @RequestParam(required = false) Long userId) {

        inventoryService.removeStock(productId, quantity, reason, referenceDocument, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/products/{productId}/stock/adjust")
    public ResponseEntity<Void> adjustStock(
            @PathVariable Long productId,
            @RequestParam Integer newQuantity,
            @RequestParam String reason,
            @RequestParam(required = false) Long userId) {

        inventoryService.adjustStock(productId, newQuantity, reason, userId);
        return ResponseEntity.ok().build();
    }

        }