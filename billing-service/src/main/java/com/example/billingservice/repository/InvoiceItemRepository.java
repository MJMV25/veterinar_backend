package com.example.billingservice.repository;

import com.example.billingservice.entity.InvoiceItem;
import com.example.billingservice.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    List<InvoiceItem> findByInvoiceId(Long invoiceId);
    List<InvoiceItem> findByServiceType(ServiceType serviceType);
    List<InvoiceItem> findByVeterinarianId(Long veterinarianId);

    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.serviceDate BETWEEN :startDate AND :endDate")
    List<InvoiceItem> findByServiceDateBetween(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(ii.total) FROM InvoiceItem ii WHERE ii.serviceType = :serviceType")
    BigDecimal getTotalAmountByServiceType(@Param("serviceType") ServiceType serviceType);

    @Query("SELECT ii.serviceType, SUM(ii.total) FROM InvoiceItem ii GROUP BY ii.serviceType")
    List<Object[]> getRevenueByServiceType();

    @Query("SELECT SUM(ii.total) FROM InvoiceItem ii WHERE ii.veterinarianId = :veterinarianId")
    BigDecimal getTotalAmountByVeterinarian(@Param("veterinarianId") Long veterinarianId);
}