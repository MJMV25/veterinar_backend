package com.example.billingservice.controller;

import com.example.billingservice.dto.InvoiceDto;
import com.example.billingservice.entity.InvoiceStatus;
import com.example.billingservice.entity.PaymentStatus;
import com.example.billingservice.service.InvoiceService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceDto> createInvoice(@Valid @RequestBody InvoiceDto invoiceDto) {
        InvoiceDto createdInvoice = invoiceService.createInvoice(invoiceDto);
        return new ResponseEntity<>(createdInvoice, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDto> updateInvoice(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceDto invoiceDto) {
        InvoiceDto updatedInvoice = invoiceService.updateInvoice(id, invoiceDto);
        return ResponseEntity.ok(updatedInvoice);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable Long id) {
        Optional<InvoiceDto> invoice = invoiceService.getInvoiceById(id);
        return invoice.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<InvoiceDto> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        Optional<InvoiceDto> invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        return invoice.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<InvoiceDto>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<InvoiceDto> invoices = invoiceService.getAllInvoices(pageable);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<Page<InvoiceDto>> getInvoicesByClient(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InvoiceDto> invoices = invoiceService.getInvoicesByClient(clientId, pageable);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<InvoiceDto>> getInvoicesByStatus(
            @PathVariable InvoiceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InvoiceDto> invoices = invoiceService.getInvoicesByStatus(status, pageable);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/payment-status/{paymentStatus}")
    public ResponseEntity<Page<InvoiceDto>> getInvoicesByPaymentStatus(
            @PathVariable PaymentStatus paymentStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InvoiceDto> invoices = invoiceService.getInvoicesByPaymentStatus(paymentStatus, pageable);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<InvoiceDto>> getInvoicesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
        Page<InvoiceDto> invoices = invoiceService.getInvoicesByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<InvoiceDto>> searchInvoices(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InvoiceDto> invoices = invoiceService.searchInvoices(searchTerm, pageable);
        return ResponseEntity.ok(invoices);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<InvoiceDto> changeInvoiceStatus(
            @PathVariable Long id,
            @RequestParam InvoiceStatus status) {
        InvoiceDto updatedInvoice = invoiceService.changeInvoiceStatus(id, status);
        return ResponseEntity.ok(updatedInvoice);
    }

    @PostMapping("/{id}/calculate-totals")
    public ResponseEntity<InvoiceDto> calculateTotals(@PathVariable Long id) {
        InvoiceDto updatedInvoice = invoiceService.calculateTotals(id);
        return ResponseEntity.ok(updatedInvoice);
    }

    @PostMapping("/{id}/apply-discount")
    public ResponseEntity<InvoiceDto> applyDiscount(
            @PathVariable Long id,
            @RequestParam BigDecimal discountPercentage) {
        InvoiceDto updatedInvoice = invoiceService.applyDiscount(id, discountPercentage);
        return ResponseEntity.ok(updatedInvoice);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<InvoiceDto>> getOverdueInvoices() {
        List<InvoiceDto> overdueInvoices = invoiceService.getOverdueInvoices();
        return ResponseEntity.ok(overdueInvoices);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getInvoiceStatistics() {
        Map<String, Object> statistics = invoiceService.getInvoiceStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/total-invoiced")
    public ResponseEntity<BigDecimal> getTotalInvoicedAmount() {
        BigDecimal total = invoiceService.getTotalInvoicedAmount();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/total-outstanding")
    public ResponseEntity<BigDecimal> getTotalOutstandingAmount() {
        BigDecimal total = invoiceService.getTotalOutstandingAmount();
        return ResponseEntity.ok(total);
    }

    @PostMapping("/mark-overdue")
    public ResponseEntity<String> markOverdueInvoices() {
        invoiceService.markOverdueInvoices();
        return ResponseEntity.ok("Overdue invoices marked successfully");
    }
}

