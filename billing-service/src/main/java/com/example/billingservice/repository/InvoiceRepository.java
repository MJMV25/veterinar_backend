package com.example.billingservice.repository;

// InvoiceRepository.java
import com.example.billingservice.entity.Invoice;
import com.example.billingservice.entity.InvoiceStatus;
import com.example.billingservice.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Basic queries
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    Page<Invoice> findByClientId(Long clientId, Pageable pageable);
    Page<Invoice> findByStatus(InvoiceStatus status, Pageable pageable);
    Page<Invoice> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);

    // Date range queries
    @Query("SELECT i FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate")
    Page<Invoice> findByIssueDateBetween(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.dueDate BETWEEN :startDate AND :endDate")
    Page<Invoice> findByDueDateBetween(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate,
                                       Pageable pageable);

    // Overdue invoices
    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :currentDate AND i.paymentStatus != 'PAID'")
    List<Invoice> findOverdueInvoices(@Param("currentDate") LocalDateTime currentDate);

    // Statistics queries
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = :status")
    Long countByStatus(@Param("status") InvoiceStatus status);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.paymentStatus = :paymentStatus")
    Long countByPaymentStatus(@Param("paymentStatus") PaymentStatus paymentStatus);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'SENT' OR i.status = 'PAID'")
    BigDecimal getTotalInvoicedAmount();

    @Query("SELECT SUM(i.paidAmount) FROM Invoice i")
    BigDecimal getTotalPaidAmount();

    @Query("SELECT SUM(i.balanceDue) FROM Invoice i WHERE i.paymentStatus != 'PAID'")
    BigDecimal getTotalOutstandingAmount();

    // Monthly/yearly reports
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE YEAR(i.issueDate) = :year AND MONTH(i.issueDate) = :month")
    BigDecimal getTotalAmountByMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE YEAR(i.issueDate) = :year")
    BigDecimal getTotalAmountByYear(@Param("year") int year);

    // Client related queries
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.clientId = :clientId")
    BigDecimal getTotalAmountByClient(@Param("clientId") Long clientId);

    @Query("SELECT SUM(i.balanceDue) FROM Invoice i WHERE i.clientId = :clientId AND i.paymentStatus != 'PAID'")
    BigDecimal getOutstandingAmountByClient(@Param("clientId") Long clientId);

    // Search functionality
    @Query("SELECT i FROM Invoice i WHERE " +
            "LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.clientName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.petName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Invoice> searchInvoices(@Param("searchTerm") String searchTerm, Pageable pageable);
}

