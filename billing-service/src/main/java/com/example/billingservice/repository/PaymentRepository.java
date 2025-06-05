package com.example.billingservice.repository;

import com.example.billingservice.entity.Payment;
import com.example.billingservice.entity.PaymentMethod;
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
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentNumber(String paymentNumber);
    List<Payment> findByInvoiceId(Long invoiceId);
    Page<Payment> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);
    Page<Payment> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    Page<Payment> findByPaymentDateBetween(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           Pageable pageable);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus = 'PAID'")
    BigDecimal getTotalPaidAmount();

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentMethod = :paymentMethod AND p.paymentStatus = 'PAID'")
    BigDecimal getTotalAmountByPaymentMethod(@Param("paymentMethod") PaymentMethod paymentMethod);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE DATE(p.paymentDate) = CURRENT_DATE AND p.paymentStatus = 'PAID'")
    BigDecimal getTodaysTotalPayments();

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE YEAR(p.paymentDate) = :year AND MONTH(p.paymentDate) = :month AND p.paymentStatus = 'PAID'")
    BigDecimal getMonthlyPayments(@Param("year") int year, @Param("month") int month);

    @Query("SELECT p.paymentMethod, SUM(p.amount) FROM Payment p WHERE p.paymentStatus = 'PAID' GROUP BY p.paymentMethod")
    List<Object[]> getPaymentsByMethod();

    @Query("SELECT p FROM Payment p WHERE p.processedBy = :userId")
    Page<Payment> findByProcessedBy(@Param("userId") Long userId, Pageable pageable);
}