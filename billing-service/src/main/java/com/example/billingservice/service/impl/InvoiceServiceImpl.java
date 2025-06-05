package com.example.billingservice.service.impl;

import com.example.billingservice.dto.InvoiceDto;
import com.example.billingservice.dto.InvoiceReportDto;
import com.example.billingservice.entity.Invoice;
import com.example.billingservice.entity.InvoiceStatus;
import com.example.billingservice.entity.PaymentStatus;
import com.example.billingservice.exception.InvoiceNotFoundException;
import com.example.billingservice.mapper.InvoiceMapper;
import com.example.billingservice.repository.InvoiceRepository;
import com.example.billingservice.service.InvoiceService;
import com.example.billingservice.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceMapper invoiceMapper;

    @Autowired
    private ReportService reportService;

    @Override
    public InvoiceDto createInvoice(InvoiceDto invoiceDto) {
        Invoice invoice = invoiceMapper.toEntity(invoiceDto);

        // Set default values if not provided
        if (invoice.getStatus() == null) {
            invoice.setStatus(InvoiceStatus.DRAFT);
        }
        if (invoice.getPaymentStatus() == null) {
            invoice.setPaymentStatus(PaymentStatus.PENDING);
        }
        if (invoice.getDueDate() == null) {
            invoice.setDueDate(LocalDateTime.now().plusDays(30)); // 30 days default
        }
        if (invoice.getTaxPercentage() == null) {
            invoice.setTaxPercentage(new BigDecimal("19.00")); // Default Colombian VAT
        }

        // Set default terms and conditions
        if (invoice.getTermsConditions() == null || invoice.getTermsConditions().isEmpty()) {
            invoice.setTermsConditions("Pago dentro de 30 días. Penalización por mora del 2% mensual.");
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);
        logger.info("Created new invoice with ID: {} and number: {}", savedInvoice.getId(), savedInvoice.getInvoiceNumber());

        return invoiceMapper.toDto(savedInvoice);
    }

    @Override
    public InvoiceDto updateInvoice(Long id, InvoiceDto invoiceDto) {
        Invoice existingInvoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with ID: " + id));

        // Prevent modification of sent/paid invoices (only allow specific changes)
        if (existingInvoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("Cannot modify a paid invoice");
        }

        // Update only allowed fields
        existingInvoice.setClientName(invoiceDto.getClientName());
        existingInvoice.setClientEmail(invoiceDto.getClientEmail());
        existingInvoice.setClientPhone(invoiceDto.getClientPhone());
        existingInvoice.setClientAddress(invoiceDto.getClientAddress());
        existingInvoice.setPetName(invoiceDto.getPetName());
        existingInvoice.setNotes(invoiceDto.getNotes());
        existingInvoice.setTermsConditions(invoiceDto.getTermsConditions());
        existingInvoice.setDueDate(invoiceDto.getDueDate());

        if (invoiceDto.getDiscountPercentage() != null) {
            existingInvoice.setDiscountPercentage(invoiceDto.getDiscountPercentage());
        }
        if (invoiceDto.getTaxPercentage() != null) {
            existingInvoice.setTaxPercentage(invoiceDto.getTaxPercentage());
        }

        // Recalculate totals
        existingInvoice.calculateTotals();

        Invoice updatedInvoice = invoiceRepository.save(existingInvoice);
        logger.info("Updated invoice with ID: {}", id);

        return invoiceMapper.toDto(updatedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InvoiceDto> getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .map(invoiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InvoiceDto> getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .map(invoiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDto> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable)
                .map(invoiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDto> getInvoicesByClient(Long clientId, Pageable pageable) {
        return invoiceRepository.findByClientId(clientId, pageable)
                .map(invoiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDto> getInvoicesByStatus(InvoiceStatus status, Pageable pageable) {
        return invoiceRepository.findByStatus(status, pageable)
                .map(invoiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDto> getInvoicesByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable) {
        return invoiceRepository.findByPaymentStatus(paymentStatus, pageable)
                .map(invoiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDto> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return invoiceRepository.findByIssueDateBetween(startDate, endDate, pageable)
                .map(invoiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDto> searchInvoices(String searchTerm, Pageable pageable) {
        return invoiceRepository.searchInvoices(searchTerm, pageable)
                .map(invoiceMapper::toDto);
    }

    @Override
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with ID: " + id));

        // Only allow deletion of draft invoices
        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Cannot delete invoice that is not in DRAFT status");
        }

        // Check if there are any payments
        if (invoice.getPayments() != null && !invoice.getPayments().isEmpty()) {
            throw new IllegalStateException("Cannot delete invoice with associated payments");
        }

        invoiceRepository.delete(invoice);
        logger.info("Deleted invoice with ID: {}", id);
    }

    @Override
    public InvoiceDto changeInvoiceStatus(Long id, InvoiceStatus newStatus) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with ID: " + id));

        // Validate status transitions
        validateStatusTransition(invoice.getStatus(), newStatus);

        invoice.setStatus(newStatus);

        // Set issue date when sending
        if (newStatus == InvoiceStatus.SENT && invoice.getIssueDate() == null) {
            invoice.setIssueDate(LocalDateTime.now());
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        logger.info("Changed invoice {} status from {} to {}", id, invoice.getStatus(), newStatus);

        return invoiceMapper.toDto(updatedInvoice);
    }

    @Override
    public InvoiceDto calculateTotals(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with ID: " + invoiceId));

        invoice.calculateTotals();
        Invoice updatedInvoice = invoiceRepository.save(invoice);

        logger.info("Recalculated totals for invoice {}: Subtotal={}, Tax={}, Total={}",
                invoiceId, invoice.getSubtotal(), invoice.getTaxAmount(), invoice.getTotalAmount());

        return invoiceMapper.toDto(updatedInvoice);
    }

    @Override
    public InvoiceDto applyDiscount(Long invoiceId, BigDecimal discountPercentage) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with ID: " + invoiceId));

        // Validate discount percentage
        if (discountPercentage.compareTo(BigDecimal.ZERO) < 0 ||
                discountPercentage.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }

        invoice.setDiscountPercentage(discountPercentage);
        invoice.calculateTotals();

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        logger.info("Applied {}% discount to invoice {}, new total: {}",
                discountPercentage, invoiceId, updatedInvoice.getTotalAmount());

        return invoiceMapper.toDto(updatedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> getOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(LocalDateTime.now());
        return overdueInvoices.stream()
                .map(invoiceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceDto sendInvoiceByEmail(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with ID: " + invoiceId));

        if (invoice.getClientEmail() == null || invoice.getClientEmail().isEmpty()) {
            throw new IllegalStateException("Client email is required to send invoice");
        }

        try {
            // Generate and send PDF invoice
            reportService.emailInvoice(invoiceId, invoice.getClientEmail());

            // Update status to SENT if it was DRAFT
            if (invoice.getStatus() == InvoiceStatus.DRAFT) {
                invoice.setStatus(InvoiceStatus.SENT);
                invoice.setIssueDate(LocalDateTime.now());
                invoiceRepository.save(invoice);
            }

            logger.info("Sent invoice {} to email: {}", invoiceId, invoice.getClientEmail());

        } catch (Exception e) {
            logger.error("Failed to send invoice {} by email: {}", invoiceId, e.getMessage());
            throw new RuntimeException("Failed to send invoice by email", e);
        }

        return invoiceMapper.toDto(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getInvoiceStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        statistics.put("totalInvoiced", getTotalInvoicedAmount());
        statistics.put("totalPaid", invoiceRepository.getTotalPaidAmount());
        statistics.put("totalOutstanding", getTotalOutstandingAmount());
        statistics.put("invoiceCountByStatus", getInvoiceCountByStatus());
        statistics.put("overdueCount", invoiceRepository.findOverdueInvoices(LocalDateTime.now()).size());

        // Monthly statistics
        LocalDateTime now = LocalDateTime.now();
        statistics.put("currentMonthTotal", invoiceRepository.getTotalAmountByMonth(now.getYear(), now.getMonthValue()));
        statistics.put("currentYearTotal", invoiceRepository.getTotalAmountByYear(now.getYear()));

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalInvoicedAmount() {
        BigDecimal total = invoiceRepository.getTotalInvoicedAmount();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalOutstandingAmount() {
        BigDecimal total = invoiceRepository.getTotalOutstandingAmount();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<InvoiceStatus, Long> getInvoiceCountByStatus() {
        Map<InvoiceStatus, Long> counts = new HashMap<>();
        for (InvoiceStatus status : InvoiceStatus.values()) {
            counts.put(status, invoiceRepository.countByStatus(status));
        }
        return counts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceReportDto> generateInvoiceReport(LocalDateTime startDate, LocalDateTime endDate) {
        Page<Invoice> invoices = invoiceRepository.findByIssueDateBetween(startDate, endDate,
                org.springframework.data.domain.Pageable.unpaged());

        return invoices.getContent().stream()
                .map(this::mapToInvoiceReportDto)
                .collect(Collectors.toList());
    }

    @Override
    @Scheduled(fixedRate = 86400000) // Daily at midnight
    public void markOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(LocalDateTime.now());

        int markedCount = 0;
        for (Invoice invoice : overdueInvoices) {
            if (invoice.getStatus() != InvoiceStatus.OVERDUE &&
                    invoice.getPaymentStatus() != PaymentStatus.PAID) {
                invoice.setStatus(InvoiceStatus.OVERDUE);
                invoiceRepository.save(invoice);
                markedCount++;
            }
        }

        if (markedCount > 0) {
            logger.info("Marked {} invoices as overdue", markedCount);
        }
    }

    @Override
    @Scheduled(fixedRate = 604800000) // Weekly
    public void sendInvoiceNotifications() {
        // Send notifications for invoices due in 7 days
        LocalDateTime dueSoon = LocalDateTime.now().plusDays(7);
        // Implementation would integrate with notification service
        logger.info("Sent invoice due soon notifications");
    }

    @Override
    @Scheduled(fixedRate = 86400000) // Daily
    public void sendOverdueReminders() {
        List<Invoice> overdueInvoices = getOverdueInvoicesForReminders();

        for (Invoice invoice : overdueInvoices) {
            try {
                // Send overdue reminder
                // Implementation would integrate with notification service
                logger.info("Sent overdue reminder for invoice: {}", invoice.getInvoiceNumber());
            } catch (Exception e) {
                logger.error("Failed to send overdue reminder for invoice {}: {}",
                        invoice.getInvoiceNumber(), e.getMessage());
            }
        }
    }

    // Helper methods
    private void validateStatusTransition(InvoiceStatus currentStatus, InvoiceStatus newStatus) {
        // Define valid status transitions
        switch (currentStatus) {
            case DRAFT:
                if (newStatus != InvoiceStatus.SENT && newStatus != InvoiceStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from DRAFT to " + newStatus);
                }
                break;
            case SENT:
                if (newStatus != InvoiceStatus.PAID && newStatus != InvoiceStatus.OVERDUE &&
                        newStatus != InvoiceStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from SENT to " + newStatus);
                }
                break;
            case PAID:
                throw new IllegalStateException("Cannot change status of a paid invoice");
            case OVERDUE:
                if (newStatus != InvoiceStatus.PAID && newStatus != InvoiceStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition from OVERDUE to " + newStatus);
                }
                break;
            case CANCELLED:
                throw new IllegalStateException("Cannot change status of a cancelled invoice");
        }
    }

    private List<Invoice> getOverdueInvoicesForReminders() {
        return invoiceRepository.findOverdueInvoices(LocalDateTime.now())
                .stream()
                .filter(invoice -> invoice.getStatus() == InvoiceStatus.OVERDUE)
                .collect(Collectors.toList());
    }

    private InvoiceReportDto mapToInvoiceReportDto(Invoice invoice) {
        InvoiceReportDto dto = new InvoiceReportDto();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setClientName(invoice.getClientName());
        dto.setClientEmail(invoice.getClientEmail());
        dto.setPetName(invoice.getPetName());
        dto.setStatus(invoice.getStatus());
        dto.setPaymentStatus(invoice.getPaymentStatus());
        dto.setSubtotal(invoice.getSubtotal());
        dto.setTaxAmount(invoice.getTaxAmount());
        dto.setDiscountAmount(invoice.getDiscountAmount());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setPaidAmount(invoice.getPaidAmount());
        dto.setBalanceDue(invoice.getBalanceDue());
        dto.setIssueDate(invoice.getIssueDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setPaymentDate(invoice.getPaymentDate());

        // Calculate services summary
        if (invoice.getItems() != null && !invoice.getItems().isEmpty()) {
            String servicesSummary = invoice.getItems().stream()
                    .map(item -> item.getDescription())
                    .collect(Collectors.joining(", "));
            dto.setServicesSummary(servicesSummary);
        }

        return dto;
    }
}