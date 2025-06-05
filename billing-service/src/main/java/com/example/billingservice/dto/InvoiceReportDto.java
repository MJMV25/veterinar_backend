package com.example.billingservice.dto;

import com.example.billingservice.entity.InvoiceStatus;
import com.example.billingservice.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceReportDto {
    private Long id;
    private String invoiceNumber;
    private String clientName;
    private String clientEmail;
    private String petName;
    private InvoiceStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceDue;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private LocalDateTime paymentDate;
    private Integer daysOverdue;
    private String veterinarianName;
    private String servicesSummary;

    // Constructors
    public InvoiceReportDto() {}

    public InvoiceReportDto(Long id, String invoiceNumber, String clientName,
                            BigDecimal totalAmount, InvoiceStatus status,
                            PaymentStatus paymentStatus, LocalDateTime issueDate) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.clientName = clientName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.issueDate = issueDate;
    }

    // Calculated fields
    public Integer getDaysOverdue() {
        if (dueDate != null && paymentStatus != PaymentStatus.PAID) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(dueDate)) {
                return (int) java.time.Duration.between(dueDate, now).toDays();
            }
        }
        return 0;
    }

    public BigDecimal getCollectionPercentage() {
        if (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal collected = paidAmount != null ? paidAmount : BigDecimal.ZERO;
            return collected.divide(totalAmount, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }

    public boolean isOverdue() {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate) &&
                paymentStatus != PaymentStatus.PAID;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public BigDecimal getBalanceDue() { return balanceDue; }
    public void setBalanceDue(BigDecimal balanceDue) { this.balanceDue = balanceDue; }

    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public void setDaysOverdue(Integer daysOverdue) { this.daysOverdue = daysOverdue; }

    public String getVeterinarianName() { return veterinarianName; }
    public void setVeterinarianName(String veterinarianName) { this.veterinarianName = veterinarianName; }

    public String getServicesSummary() { return servicesSummary; }
    public void setServicesSummary(String servicesSummary) { this.servicesSummary = servicesSummary; }
}