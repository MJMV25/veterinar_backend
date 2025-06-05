package com.example.inventoryservice.config;

import com.example.inventoryservice.entity.Product;
import com.example.inventoryservice.entity.ProductCategory;
import com.example.inventoryservice.entity.ProductStatus;
import com.example.inventoryservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            initializeProducts();
        }
    }

    private void initializeProducts() {
        // Sample products for testing
        Product product1 = new Product();
        product1.setProductCode("MED-001");
        product1.setProductName("Amoxicilina 500mg");
        product1.setDescription("Antibiótico de amplio espectro");
        product1.setCategory(ProductCategory.MEDICAMENTOS);
        product1.setCurrentStock(100);
        product1.setMinimumStock(20);
        product1.setMaximumStock(500);
        product1.setUnitPrice(new BigDecimal("15.50"));
        product1.setCostPrice(new BigDecimal("8.00"));
        product1.setUnitOfMeasure("Tabletas");
        product1.setBrand("VetPharma");
        product1.setSupplierName("Distribuidora Veterinaria S.A.");
        product1.setLocation("Estante A1");
        product1.setStatus(ProductStatus.ACTIVE);

        Product product2 = new Product();
        product2.setProductCode("VAC-001");
        product2.setProductName("Vacuna Triple Felina");
        product2.setDescription("Vacuna contra rinotraqueitis, calicivirus y panleucopenia");
        product2.setCategory(ProductCategory.VACUNAS);
        product2.setCurrentStock(50);
        product2.setMinimumStock(10);
        product2.setMaximumStock(200);
        product2.setUnitPrice(new BigDecimal("45.00"));
        product2.setCostPrice(new BigDecimal("25.00"));
        product2.setUnitOfMeasure("Dosis");
        product2.setBrand("VetVaccines");
        product2.setSupplierName("Laboratorio Veterinario Colombia");
        product2.setLocation("Refrigerador B");
        product2.setStatus(ProductStatus.ACTIVE);

        Product product3 = new Product();
        product3.setProductCode("ALI-001");
        product3.setProductName("Concentrado Premium Adulto");
        product3.setDescription("Alimento balanceado para perros adultos");
        product3.setCategory(ProductCategory.ALIMENTOS);
        product3.setCurrentStock(25);
        product3.setMinimumStock(5);
        product3.setMaximumStock(100);
        product3.setUnitPrice(new BigDecimal("85.000"));
        product3.setCostPrice(new BigDecimal("60.000"));
        product3.setUnitOfMeasure("Bulto 15kg");
        product3.setBrand("DogNutrition");
        product3.setSupplierName("Alimentos para Mascotas Ltda.");
        product3.setLocation("Bodega C");
        product3.setStatus(ProductStatus.ACTIVE);

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        System.out.println("✅ Sample products initialized successfully!");
    }
}