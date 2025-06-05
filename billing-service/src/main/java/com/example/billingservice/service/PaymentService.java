package com.example.billingservice.service;


import com.example.billingservice.dto.PaymentDto;
import com.example.billingservice.entity.PaymentMethod;
import com.example.billingservice.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PaymentService {
    PaymentDto createPayment(PaymentDto paymentDto);
    PaymentDto updatePayment(Long id, PaymentDto paymentDto);
    Optional<PaymentDto> getPaymentById(Long id);
    Optional<PaymentDto> getPaymentByNumber(String paymentNumber);
    Page<PaymentDto> getAllPayments(Pageable pageable);
    List<PaymentDto> getPaymentsByInvoice(Long invoiceId);
    Page<PaymentDto> getPaymentsByStatus(PaymentStatus status, Pageable pageable);
    Page<PaymentDto> getPaymentsByMethod(PaymentMethod method, Pageable pageable);
    Page<PaymentDto> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    void deletePayment(Long id);
    PaymentDto processPayment(Long paymentId);
    PaymentDto refundPayment(Long paymentId, BigDecimal refundAmount);

    // Statistics
    BigDecimal getTotalPayments();
    BigDecimal getTodaysPayments();
    Map<PaymentMethod, BigDecimal> getPaymentsByMethod();
    Map<String, Object> getPaymentStatistics();
}

