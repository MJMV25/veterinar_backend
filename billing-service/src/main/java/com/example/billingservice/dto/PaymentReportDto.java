package com.example.billingservice.dto;

import com.example.billingservice.entity.PaymentMethod;
import com.example.billingservice.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Duration;

public class PaymentReportDto {
    private Long id;
    private String paymentNumber;
    private Long invoiceId;
    private String invoiceNumber;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String petName;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;
    private String referenceNumber;
    private String transactionId;
    private String processedByName;
    private Long processedBy;
    private String notes;
    private Integer daysSincePayment;
    private BigDecimal invoiceTotal;
    private BigDecimal remainingBalance;

    // Additional calculated fields
    private BigDecimal paymentPercentage;
    private String paymentType; // FULL, PARTIAL, REFUND
    private Boolean isRefund;
    private Boolean isPartialPayment;
    private String clientFullName;
    private String formattedAmount;
    private String formattedPaymentDate;
    private String paymentMethodDescription;
    private String paymentStatusDescription;

    // Constructors
    public PaymentReportDto() {}

    public PaymentReportDto(Long id, String paymentNumber, BigDecimal amount,
                            PaymentMethod paymentMethod, PaymentStatus paymentStatus,
                            LocalDateTime paymentDate, String clientName) {
        this.id = id;
        this.paymentNumber = paymentNumber;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.clientName = clientName;

        // Set calculated fields
        calculateDerivedFields();
    }

    public PaymentReportDto(Long id, String paymentNumber, String invoiceNumber,
                            String clientName, BigDecimal amount, PaymentMethod paymentMethod,
                            PaymentStatus paymentStatus, LocalDateTime paymentDate,
                            BigDecimal invoiceTotal, BigDecimal remainingBalance) {
        this.id = id;
        this.paymentNumber = paymentNumber;
        this.invoiceNumber = invoiceNumber;
        this.clientName = clientName;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.invoiceTotal = invoiceTotal;
        this.remainingBalance = remainingBalance;

        // Set calculated fields
        calculateDerivedFields();
    }

    // Calculated methods
    public Integer getDaysSincePayment() {
        if (paymentDate != null) {
            return (int) Duration.between(paymentDate, LocalDateTime.now()).toDays();
        }
        return null;
    }

    public BigDecimal getPaymentPercentage() {
        if (invoiceTotal != null && invoiceTotal.compareTo(BigDecimal.ZERO) > 0 && amount != null) {
            // For refunds (negative amounts), calculate percentage differently
            BigDecimal absAmount = amount.abs();
            return absAmount.divide(invoiceTotal, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }

    public boolean isPartialPayment() {
        return remainingBalance != null && remainingBalance.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isRefund() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public String getPaymentType() {
        if (amount == null) return "UNKNOWN";

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return "REFUND";
        } else if (isPartialPayment()) {
            return "PARTIAL";
        } else {
            return "FULL";
        }
    }

    public String getClientFullName() {
        return clientName != null ? clientName : "Cliente Desconocido";
    }

    public String getFormattedAmount() {
        if (amount == null) return "$0.00";
        return String.format("$%,.2f", amount);
    }

    public String getFormattedPaymentDate() {
        if (paymentDate == null) return "N/A";
        return paymentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getPaymentMethodDescription() {
        return paymentMethod != null ? paymentMethod.getDescription() : "N/A";
    }

    public String getPaymentStatusDescription() {
        return paymentStatus != null ? paymentStatus.getDescription() : "N/A";
    }

    public String getPaymentSummary() {
        return String.format("%s - %s - %s (%s)",
                paymentNumber != null ? paymentNumber : "N/A",
                getFormattedAmount(),
                getPaymentMethodDescription(),
                getPaymentStatusDescription());
    }

    public String getInvoicePaymentSummary() {
        if (invoiceTotal == null) return "N/A";

        BigDecimal paidAmount = invoiceTotal.subtract(remainingBalance != null ? remainingBalance : BigDecimal.ZERO);
        BigDecimal percentage = getPaymentPercentage();

        return String.format("Factura: %s | Total: $%,.2f | Pagado: $%,.2f (%.1f%%) | Pendiente: $%,.2f",
                invoiceNumber != null ? invoiceNumber : "N/A",
                invoiceTotal,
                paidAmount,
                percentage,
                remainingBalance != null ? remainingBalance : BigDecimal.ZERO);
    }

    public String getTimeSincePayment() {
        Integer days = getDaysSincePayment();
        if (days == null) return "N/A";

        if (days == 0) return "Hoy";
        if (days == 1) return "Ayer";
        if (days < 7) return days + " días";
        if (days < 30) return (days / 7) + " semanas";
        if (days < 365) return (days / 30) + " meses";
        return (days / 365) + " años";
    }

    public String getPaymentEfficiency() {
        if (paymentStatus == PaymentStatus.PAID) {
            return "EXITOSO";
        } else if (paymentStatus == PaymentStatus.FAILED) {
            return "FALLIDO";
        } else if (paymentStatus == PaymentStatus.REFUNDED) {
            return "REEMBOLSADO";
        } else {
            return "PENDIENTE";
        }
    }

    // Private method to calculate derived fields
    private void calculateDerivedFields() {
        this.daysSincePayment = getDaysSincePayment();
        this.paymentPercentage = getPaymentPercentage();
        this.paymentType = getPaymentType();
        this.isRefund = isRefund();
        this.isPartialPayment = isPartialPayment();
        this.clientFullName = getClientFullName();
        this.formattedAmount = getFormattedAmount();
        this.formattedPaymentDate = getFormattedPaymentDate();
        this.paymentMethodDescription = getPaymentMethodDescription();
        this.paymentStatusDescription = getPaymentStatusDescription();
    }

    // Method to update calculated fields (call after setting new values)
    public void updateCalculatedFields() {
        calculateDerivedFields();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaymentNumber() { return paymentNumber; }
    public void setPaymentNumber(String paymentNumber) { this.paymentNumber = paymentNumber; }

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) {
        this.clientName = clientName;
        this.clientFullName = getClientFullName();
    }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        updateCalculatedFields();
    }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        this.paymentMethodDescription = getPaymentMethodDescription();
    }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
        this.paymentStatusDescription = getPaymentStatusDescription();
    }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
        this.daysSincePayment = getDaysSincePayment();
        this.formattedPaymentDate = getFormattedPaymentDate();
    }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getProcessedByName() { return processedByName; }
    public void setProcessedByName(String processedByName) { this.processedByName = processedByName; }

    public Long getProcessedBy() { return processedBy; }
    public void setProcessedBy(Long processedBy) { this.processedBy = processedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public void setDaysSincePayment(Integer daysSincePayment) { this.daysSincePayment = daysSincePayment; }

    public BigDecimal getInvoiceTotal() { return invoiceTotal; }
    public void setInvoiceTotal(BigDecimal invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
        this.paymentPercentage = getPaymentPercentage();
    }

    public BigDecimal getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
        this.isPartialPayment = isPartialPayment();
        this.paymentType = getPaymentType();
    }

    // Getters for calculated fields
    public void setPaymentPercentage(BigDecimal paymentPercentage) { this.paymentPercentage = paymentPercentage; }

    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public Boolean getIsRefund() { return isRefund; }
    public void setIsRefund(Boolean isRefund) { this.isRefund = isRefund; }

    public Boolean getIsPartialPayment() { return isPartialPayment; }
    public void setIsPartialPayment(Boolean isPartialPayment) { this.isPartialPayment = isPartialPayment; }

    public void setClientFullName(String clientFullName) { this.clientFullName = clientFullName; }

    public void setFormattedAmount(String formattedAmount) { this.formattedAmount = formattedAmount; }

    public void setFormattedPaymentDate(String formattedPaymentDate) { this.formattedPaymentDate = formattedPaymentDate; }

    public void setPaymentMethodDescription(String paymentMethodDescription) { this.paymentMethodDescription = paymentMethodDescription; }

    public void setPaymentStatusDescription(String paymentStatusDescription) { this.paymentStatusDescription = paymentStatusDescription; }

    // Override toString for easy debugging
    @Override
    public String toString() {
        return "PaymentReportDto{" +
                "id=" + id +
                ", paymentNumber='" + paymentNumber + '\'' +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", clientName='" + clientName + '\'' +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                ", paymentStatus=" + paymentStatus +
                ", paymentDate=" + paymentDate +
                ", paymentType='" + paymentType + '\'' +
                '}';
    }

    // Equals and HashCode based on ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentReportDto that = (PaymentReportDto) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}