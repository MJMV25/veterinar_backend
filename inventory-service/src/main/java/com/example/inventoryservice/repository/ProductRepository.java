package com.example.inventoryservice.repository;


import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.entity.ProductCategory;
import com.example.inventoryservice.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductCode(String productCode);
    boolean existsByProductCode(String productCode);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
    Page<Product> findByCategory(ProductCategory category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:name% OR p.productCode LIKE %:name%")
    Page<Product> findByNameOrCodeContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.brand LIKE %:brand%")
    Page<Product> findByBrandContaining(@Param("brand") String brand, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.supplierName LIKE %:supplier%")
    Page<Product> findBySupplierContaining(@Param("supplier") String supplier, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.currentStock <= p.minimumStock AND p.status = 'ACTIVE'")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.expirationDate <= :date AND p.status = 'ACTIVE'")
    List<Product> findExpiredProducts(@Param("date") LocalDate date);

    @Query("SELECT p FROM Product p WHERE p.expirationDate BETWEEN :startDate AND :endDate AND p.status = 'ACTIVE'")
    List<Product> findProductsExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM Product p WHERE p.currentStock = 0 AND p.status = 'ACTIVE'")
    List<Product> findOutOfStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = :status")
    Long countByStatus(@Param("status") ProductStatus status);

    @Query("SELECT COALESCE(SUM(p.currentStock * p.unitPrice), 0) FROM Product p WHERE p.status = 'ACTIVE'")
    BigDecimal calculateTotalInventoryValue();
}