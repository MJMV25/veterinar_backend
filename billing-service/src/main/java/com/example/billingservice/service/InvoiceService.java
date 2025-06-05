package com.example.billingservice.service;

import com.example.billingservice.dto.InvoiceDto;
import com.example.billingservice.dto.InvoiceReportDto;
import com.example.billingservice.entity.InvoiceStatus;
import com.example.billingservice.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InvoiceService {
    InvoiceDto createInvoice(InvoiceDto invoiceDto);
    InvoiceDto updateInvoice(Long id, InvoiceDto invoiceDto);
    Optional<InvoiceDto> getInvoiceById(Long id);
    Optional<InvoiceDto> getInvoiceByNumber(String invoiceNumber);
    Page<InvoiceDto> getAllInvoices(Pageable pageable);
    Page<InvoiceDto> getInvoicesByClient(Long clientId, Pageable pageable);
    Page<InvoiceDto> getInvoicesByStatus(InvoiceStatus status, Pageable pageable);
    Page<InvoiceDto> getInvoicesByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);
    Page<InvoiceDto> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<InvoiceDto> searchInvoices(String searchTerm, Pageable pageable);

    void deleteInvoice(Long id);
    InvoiceDto changeInvoiceStatus(Long id, InvoiceStatus newStatus);

    // Business logic methods
    InvoiceDto calculateTotals(Long invoiceId);
    InvoiceDto applyDiscount(Long invoiceId, BigDecimal discountPercentage);
    List<InvoiceDto> getOverdueInvoices();

    InvoiceDto sendInvoiceByEmail(Long invoiceId);

    // Statistics
    Map<String, Object> getInvoiceStatistics();
    BigDecimal getTotalInvoicedAmount();
    BigDecimal getTotalOutstandingAmount();
    Map<InvoiceStatus, Long> getInvoiceCountByStatus();

    @Transactional(readOnly = true)
    List<InvoiceReportDto> generateInvoiceReport(LocalDateTime startDate, LocalDateTime endDate);

    // Automated processes
    void markOverdueInvoices();
    void sendInvoiceNotifications();

    @Scheduled(fixedRate = 86400000) // Daily
    void sendOverdueReminders();
}
