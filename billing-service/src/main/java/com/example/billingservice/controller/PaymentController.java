package com.example.billingservice.controller;


import com.example.billingservice.dto.PaymentDto;
import com.example.billingservice.entity.PaymentMethod;
import com.example.billingservice.entity.PaymentStatus;
import com.example.billingservice.service.PaymentService;
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
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@Valid @RequestBody PaymentDto paymentDto) {
        PaymentDto createdPayment = paymentService.createPayment(paymentDto);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDto> updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentDto paymentDto) {
        PaymentDto updatedPayment = paymentService.updatePayment(id, paymentDto);
        return ResponseEntity.ok(updatedPayment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id) {
        Optional<PaymentDto> payment = paymentService.getPaymentById(id);
        return payment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{paymentNumber}")
    public ResponseEntity<PaymentDto> getPaymentByNumber(@PathVariable String paymentNumber) {
        Optional<PaymentDto> payment = paymentService.getPaymentByNumber(paymentNumber);
        return payment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<PaymentDto>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PaymentDto> payments = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByInvoice(@PathVariable Long invoiceId) {
        List<PaymentDto> payments = paymentService.getPaymentsByInvoice(invoiceId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<PaymentDto>> getPaymentsByStatus(
            @PathVariable PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        Page<PaymentDto> payments = paymentService.getPaymentsByStatus(status, pageable);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/method/{method}")
    public ResponseEntity<Page<PaymentDto>> getPaymentsByMethod(
            @PathVariable PaymentMethod method,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        Page<PaymentDto> payments = paymentService.getPaymentsByMethod(method, pageable);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<PaymentDto>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        Page<PaymentDto> payments = paymentService.getPaymentsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(payments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<PaymentDto> processPayment(@PathVariable Long id) {
        PaymentDto processedPayment = paymentService.processPayment(id);
        return ResponseEntity.ok(processedPayment);
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentDto> refundPayment(
            @PathVariable Long id,
            @RequestParam BigDecimal refundAmount) {
        PaymentDto refundPayment = paymentService.refundPayment(id, refundAmount);
        return ResponseEntity.ok(refundPayment);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getPaymentStatistics() {
        Map<String, Object> statistics = paymentService.getPaymentStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalPayments() {
        BigDecimal total = paymentService.getTotalPayments();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/today")
    public ResponseEntity<BigDecimal> getTodaysPayments() {
        BigDecimal total = paymentService.getTodaysPayments();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/by-method")
    public ResponseEntity<Map<PaymentMethod, BigDecimal>> getPaymentsByMethod() {
        Map<PaymentMethod, BigDecimal> paymentsByMethod = paymentService.getPaymentsByMethod();
        return ResponseEntity.ok(paymentsByMethod);
    }
}