package com.example.billingservice.service.impl;

import com.example.billingservice.dto.PaymentDto;
import com.example.billingservice.entity.*;
import com.example.billingservice.exception.PaymentException;
import com.example.billingservice.mapper.PaymentMapper;
import com.example.billingservice.repository.InvoiceRepository;
import com.example.billingservice.repository.PaymentRepository;
import com.example.billingservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public PaymentDto createPayment(PaymentDto paymentDto) {
        Payment payment = paymentMapper.toEntity(paymentDto);

        // Validate invoice exists
        Invoice invoice = invoiceRepository.findById(paymentDto.getInvoiceId())
                .orElseThrow(() -> new PaymentException("Invoice not found with ID: " + paymentDto.getInvoiceId()));

        payment.setInvoice(invoice);

        // Set default status if not provided
        if (payment.getPaymentStatus() == null) {
            payment.setPaymentStatus(PaymentStatus.PENDING);
        }

        Payment savedPayment = paymentRepository.save(payment);

        // Update invoice paid amount and status
        updateInvoicePaymentStatus(invoice);

        logger.info("Created new payment with ID: {}", savedPayment.getId());

        return paymentMapper.toDto(savedPayment);
    }

    @Override
    public PaymentDto updatePayment(Long id, PaymentDto paymentDto) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentException("Payment not found with ID: " + id));

        // Update allowed fields
        existingPayment.setAmount(paymentDto.getAmount());
        existingPayment.setPaymentMethod(paymentDto.getPaymentMethod());
        existingPayment.setPaymentDate(paymentDto.getPaymentDate());
        existingPayment.setReferenceNumber(paymentDto.getReferenceNumber());
        existingPayment.setTransactionId(paymentDto.getTransactionId());
        existingPayment.setNotes(paymentDto.getNotes());

        Payment updatedPayment = paymentRepository.save(existingPayment);

        // Update invoice payment status
        updateInvoicePaymentStatus(existingPayment.getInvoice());

        logger.info("Updated payment with ID: {}", id);

        return paymentMapper.toDto(updatedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDto> getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(paymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDto> getPaymentByNumber(String paymentNumber) {
        return paymentRepository.findByPaymentNumber(paymentNumber)
                .map(paymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDto> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(paymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByInvoice(Long invoiceId) {
        List<Payment> payments = paymentRepository.findByInvoiceId(invoiceId);
        return payments.stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDto> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        return paymentRepository.findByPaymentStatus(status, pageable)
                .map(paymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDto> getPaymentsByMethod(PaymentMethod method, Pageable pageable) {
        return paymentRepository.findByPaymentMethod(method, pageable)
                .map(paymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDto> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate, pageable)
                .map(paymentMapper::toDto);
    }

    @Override
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentException("Payment not found with ID: " + id));

        // Only allow deletion of pending payments
        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Cannot delete processed payment");
        }

        Invoice invoice = payment.getInvoice();
        paymentRepository.delete(payment);

        // Update invoice payment status
        updateInvoicePaymentStatus(invoice);

        logger.info("Deleted payment with ID: {}", id);
    }

    @Override
    public PaymentDto processPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException("Payment not found with ID: " + paymentId));

        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDateTime.now());

        Payment processedPayment = paymentRepository.save(payment);

        // Update invoice payment status
        updateInvoicePaymentStatus(payment.getInvoice());

        logger.info("Processed payment with ID: {}", paymentId);

        return paymentMapper.toDto(processedPayment);
    }

    @Override
    public PaymentDto refundPayment(Long paymentId, BigDecimal refundAmount) {
        Payment originalPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException("Payment not found with ID: " + paymentId));

        if (originalPayment.getPaymentStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("Can only refund paid payments");
        }

        if (refundAmount.compareTo(originalPayment.getAmount()) > 0) {
            throw new IllegalArgumentException("Refund amount cannot exceed original payment amount");
        }

        // Create refund payment
        Payment refund = new Payment();
        refund.setInvoice(originalPayment.getInvoice());
        refund.setAmount(refundAmount.negate()); // Negative amount for refund
        refund.setPaymentMethod(originalPayment.getPaymentMethod());
        refund.setPaymentStatus(PaymentStatus.REFUNDED);
        refund.setPaymentDate(LocalDateTime.now());
        refund.setReferenceNumber("REFUND-" + originalPayment.getPaymentNumber());
        refund.setNotes("Refund for payment: " + originalPayment.getPaymentNumber());

        Payment savedRefund = paymentRepository.save(refund);

        // Update invoice payment status
        updateInvoicePaymentStatus(originalPayment.getInvoice());

        logger.info("Created refund payment with ID: {} for original payment: {}",
                savedRefund.getId(), paymentId);

        return paymentMapper.toDto(savedRefund);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPayments() {
        BigDecimal total = paymentRepository.getTotalPaidAmount();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTodaysPayments() {
        BigDecimal total = paymentRepository.getTodaysTotalPayments();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<PaymentMethod, BigDecimal> getPaymentsByMethod() {
        List<Object[]> results = paymentRepository.getPaymentsByMethod();
        Map<PaymentMethod, BigDecimal> paymentsByMethod = new HashMap<>();

        for (Object[] result : results) {
            PaymentMethod method = (PaymentMethod) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            paymentsByMethod.put(method, amount);
        }

        return paymentsByMethod;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getPaymentStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        statistics.put("totalPayments", getTotalPayments());
        statistics.put("todaysPayments", getTodaysPayments());
        statistics.put("paymentsByMethod", getPaymentsByMethod());

        return statistics;
    }

    private void updateInvoicePaymentStatus(Invoice invoice) {
        // Calculate total paid amount
        BigDecimal totalPaid = paymentRepository.findByInvoiceId(invoice.getId())
                .stream()
                .filter(payment -> payment.getPaymentStatus() == PaymentStatus.PAID)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setPaidAmount(totalPaid);
        invoice.calculateTotals(); // This will update payment status and balance due

        invoiceRepository.save(invoice);
    }
}