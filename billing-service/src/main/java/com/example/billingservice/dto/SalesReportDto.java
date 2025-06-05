package com.example.billingservice.dto;

import com.example.billingservice.entity.ServiceType;
import com.example.billingservice.entity.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SalesReportDto {
    private LocalDateTime reportDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String reportPeriod; // DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM

    // Summary totals
    private BigDecimal totalSales;
    private BigDecimal totalPaid;
    private BigDecimal totalOutstanding;
    private BigDecimal totalTax;
    private BigDecimal totalDiscount;
    private Integer totalInvoices;
    private Integer paidInvoices;
    private Integer pendingInvoices;
    private Integer overdueInvoices;
    private Integer cancelledInvoices;

    // Performance metrics
    private BigDecimal collectionRate; // Percentage of collected payments
    private BigDecimal averageInvoiceAmount;
    private BigDecimal averagePaymentTime; // Days to payment

    // Breakdown by service type
    private List<ServiceSalesDto> salesByService;

    // Breakdown by payment method
    private List<PaymentMethodSalesDto> salesByPaymentMethod;

    // Top clients
    private List<ClientSalesDto> topClients;

    // Monthly breakdown (for yearly reports)
    private List<MonthlySalesDto> monthlyBreakdown;

    // Growth metrics
    private BigDecimal growthPercentage;
    private String growthComparison; // "vs previous period"
    private BigDecimal previousPeriodSales;

    // Constructors
    public SalesReportDto() {
        this.reportDate = LocalDateTime.now();
    }

    public SalesReportDto(LocalDateTime startDate, LocalDateTime endDate, String reportPeriod) {
        this();
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportPeriod = reportPeriod;
    }

    // Calculated methods
    public BigDecimal getCollectionRate() {
        if (totalSales != null && totalSales.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal paid = totalPaid != null ? totalPaid : BigDecimal.ZERO;
            return paid.divide(totalSales, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getAverageInvoiceAmount() {
        if (totalInvoices != null && totalInvoices > 0 && totalSales != null) {
            return totalSales.divide(new BigDecimal(totalInvoices), 2, java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public String getReportSummary() {
        return String.format("Período: %s - %s | Total Ventas: $%s | Facturado: %d | Cobrado: $%s",
                startDate != null ? startDate.toLocalDate().toString() : "N/A",
                endDate != null ? endDate.toLocalDate().toString() : "N/A",
                totalSales != null ? totalSales.toString() : "0",
                totalInvoices != null ? totalInvoices : 0,
                totalPaid != null ? totalPaid.toString() : "0");
    }

    // Inner DTOs
    public static class ServiceSalesDto {
        private ServiceType serviceType;
        private String serviceDescription;
        private BigDecimal totalAmount;
        private Integer quantity;
        private BigDecimal percentage;
        private BigDecimal averagePrice;

        public ServiceSalesDto() {}

        public ServiceSalesDto(ServiceType serviceType, BigDecimal totalAmount, Integer quantity) {
            this.serviceType = serviceType;
            this.serviceDescription = serviceType != null ? serviceType.getDescription() : "N/A";
            this.totalAmount = totalAmount;
            this.quantity = quantity;
            this.averagePrice = quantity != null && quantity > 0 ?
                    totalAmount.divide(new BigDecimal(quantity), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }

        // Getters and Setters
        public ServiceType getServiceType() { return serviceType; }
        public void setServiceType(ServiceType serviceType) {
            this.serviceType = serviceType;
            this.serviceDescription = serviceType != null ? serviceType.getDescription() : "N/A";
        }

        public String getServiceDescription() { return serviceDescription; }
        public void setServiceDescription(String serviceDescription) { this.serviceDescription = serviceDescription; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getPercentage() { return percentage; }
        public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }

        public BigDecimal getAveragePrice() { return averagePrice; }
        public void setAveragePrice(BigDecimal averagePrice) { this.averagePrice = averagePrice; }
    }

    public static class PaymentMethodSalesDto {
        private PaymentMethod paymentMethod;
        private String methodDescription;
        private BigDecimal totalAmount;
        private Integer transactionCount;
        private BigDecimal percentage;
        private BigDecimal averageTransaction;

        public PaymentMethodSalesDto() {}

        public PaymentMethodSalesDto(PaymentMethod paymentMethod, BigDecimal totalAmount, Integer transactionCount) {
            this.paymentMethod = paymentMethod;
            this.methodDescription = paymentMethod != null ? paymentMethod.getDescription() : "N/A";
            this.totalAmount = totalAmount;
            this.transactionCount = transactionCount;
            this.averageTransaction = transactionCount != null && transactionCount > 0 ?
                    totalAmount.divide(new BigDecimal(transactionCount), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }

        // Getters and Setters
        public PaymentMethod getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            this.methodDescription = paymentMethod != null ? paymentMethod.getDescription() : "N/A";
        }

        public String getMethodDescription() { return methodDescription; }
        public void setMethodDescription(String methodDescription) { this.methodDescription = methodDescription; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public Integer getTransactionCount() { return transactionCount; }
        public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }

        public BigDecimal getPercentage() { return percentage; }
        public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }

        public BigDecimal getAverageTransaction() { return averageTransaction; }
        public void setAverageTransaction(BigDecimal averageTransaction) { this.averageTransaction = averageTransaction; }
    }

    public static class ClientSalesDto {
        private Long clientId;
        private String clientName;
        private String clientEmail;
        private BigDecimal totalSpent;
        private Integer invoiceCount;
        private LocalDateTime lastVisit;
        private BigDecimal averageInvoice;
        private String loyaltyLevel;

        public ClientSalesDto() {}

        public ClientSalesDto(Long clientId, String clientName, BigDecimal totalSpent, Integer invoiceCount) {
            this.clientId = clientId;
            this.clientName = clientName;
            this.totalSpent = totalSpent;
            this.invoiceCount = invoiceCount;
            this.averageInvoice = invoiceCount != null && invoiceCount > 0 ?
                    totalSpent.divide(new BigDecimal(invoiceCount), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
            this.loyaltyLevel = calculateLoyaltyLevel(totalSpent, invoiceCount);
        }

        private String calculateLoyaltyLevel(BigDecimal totalSpent, Integer invoiceCount) {
            if (totalSpent == null) return "NEW";

            BigDecimal amount = totalSpent;
            if (amount.compareTo(new BigDecimal("1000000")) >= 0) return "PLATINUM";
            if (amount.compareTo(new BigDecimal("500000")) >= 0) return "GOLD";
            if (amount.compareTo(new BigDecimal("200000")) >= 0) return "SILVER";
            if (amount.compareTo(new BigDecimal("50000")) >= 0) return "BRONZE";
            return "BASIC";
        }

        // Getters and Setters
        public Long getClientId() { return clientId; }
        public void setClientId(Long clientId) { this.clientId = clientId; }

        public String getClientName() { return clientName; }
        public void setClientName(String clientName) { this.clientName = clientName; }

        public String getClientEmail() { return clientEmail; }
        public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

        public BigDecimal getTotalSpent() { return totalSpent; }
        public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }

        public Integer getInvoiceCount() { return invoiceCount; }
        public void setInvoiceCount(Integer invoiceCount) { this.invoiceCount = invoiceCount; }

        public LocalDateTime getLastVisit() { return lastVisit; }
        public void setLastVisit(LocalDateTime lastVisit) { this.lastVisit = lastVisit; }

        public BigDecimal getAverageInvoice() { return averageInvoice; }
        public void setAverageInvoice(BigDecimal averageInvoice) { this.averageInvoice = averageInvoice; }

        public String getLoyaltyLevel() { return loyaltyLevel; }
        public void setLoyaltyLevel(String loyaltyLevel) { this.loyaltyLevel = loyaltyLevel; }
    }

    public static class MonthlySalesDto {
        private Integer year;
        private Integer month;
        private String monthName;
        private BigDecimal totalSales;
        private BigDecimal totalPaid;
        private Integer invoiceCount;
        private BigDecimal growthPercentage;

        public MonthlySalesDto() {}

        public MonthlySalesDto(Integer year, Integer month, BigDecimal totalSales, BigDecimal totalPaid, Integer invoiceCount) {
            this.year = year;
            this.month = month;
            this.monthName = getMonthName(month);
            this.totalSales = totalSales;
            this.totalPaid = totalPaid;
            this.invoiceCount = invoiceCount;
        }

        private String getMonthName(Integer month) {
            String[] months = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
            return month != null && month >= 1 && month <= 12 ? months[month] : "Desconocido";
        }

        // Getters and Setters
        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }

        public Integer getMonth() { return month; }
        public void setMonth(Integer month) {
            this.month = month;
            this.monthName = getMonthName(month);
        }

        public String getMonthName() { return monthName; }
        public void setMonthName(String monthName) { this.monthName = monthName; }

        public BigDecimal getTotalSales() { return totalSales; }
        public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }

        public BigDecimal getTotalPaid() { return totalPaid; }
        public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }

        public Integer getInvoiceCount() { return invoiceCount; }
        public void setInvoiceCount(Integer invoiceCount) { this.invoiceCount = invoiceCount; }

        public BigDecimal getGrowthPercentage() { return growthPercentage; }
        public void setGrowthPercentage(BigDecimal growthPercentage) { this.growthPercentage = growthPercentage; }
    }

    // Main class getters and setters
    public LocalDateTime getReportDate() { return reportDate; }
    public void setReportDate(LocalDateTime reportDate) { this.reportDate = reportDate; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getReportPeriod() { return reportPeriod; }
    public void setReportPeriod(String reportPeriod) { this.reportPeriod = reportPeriod; }

    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }

    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }

    public BigDecimal getTotalOutstanding() { return totalOutstanding; }
    public void setTotalOutstanding(BigDecimal totalOutstanding) { this.totalOutstanding = totalOutstanding; }

    public BigDecimal getTotalTax() { return totalTax; }
    public void setTotalTax(BigDecimal totalTax) { this.totalTax = totalTax; }

    public BigDecimal getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(BigDecimal totalDiscount) { this.totalDiscount = totalDiscount; }

    public Integer getTotalInvoices() { return totalInvoices; }
    public void setTotalInvoices(Integer totalInvoices) { this.totalInvoices = totalInvoices; }

    public Integer getPaidInvoices() { return paidInvoices; }
    public void setPaidInvoices(Integer paidInvoices) { this.paidInvoices = paidInvoices; }

    public Integer getPendingInvoices() { return pendingInvoices; }
    public void setPendingInvoices(Integer pendingInvoices) { this.pendingInvoices = pendingInvoices; }

    public Integer getOverdueInvoices() { return overdueInvoices; }
    public void setOverdueInvoices(Integer overdueInvoices) { this.overdueInvoices = overdueInvoices; }

    public Integer getCancelledInvoices() { return cancelledInvoices; }
    public void setCancelledInvoices(Integer cancelledInvoices) { this.cancelledInvoices = cancelledInvoices; }

    public void setCollectionRate(BigDecimal collectionRate) { this.collectionRate = collectionRate; }

    public void setAverageInvoiceAmount(BigDecimal averageInvoiceAmount) { this.averageInvoiceAmount = averageInvoiceAmount; }

    public BigDecimal getAveragePaymentTime() { return averagePaymentTime; }
    public void setAveragePaymentTime(BigDecimal averagePaymentTime) { this.averagePaymentTime = averagePaymentTime; }

    public List<ServiceSalesDto> getSalesByService() { return salesByService; }
    public void setSalesByService(List<ServiceSalesDto> salesByService) { this.salesByService = salesByService; }

    public List<PaymentMethodSalesDto> getSalesByPaymentMethod() { return salesByPaymentMethod; }
    public void setSalesByPaymentMethod(List<PaymentMethodSalesDto> salesByPaymentMethod) { this.salesByPaymentMethod = salesByPaymentMethod; }

    public List<ClientSalesDto> getTopClients() { return topClients; }
    public void setTopClients(List<ClientSalesDto> topClients) { this.topClients = topClients; }

    public List<MonthlySalesDto> getMonthlyBreakdown() { return monthlyBreakdown; }
    public void setMonthlyBreakdown(List<MonthlySalesDto> monthlyBreakdown) { this.monthlyBreakdown = monthlyBreakdown; }

    public BigDecimal getGrowthPercentage() { return growthPercentage; }
    public void setGrowthPercentage(BigDecimal growthPercentage) { this.growthPercentage = growthPercentage; }

    public String getGrowthComparison() { return growthComparison; }
    public void setGrowthComparison(String growthComparison) { this.growthComparison = growthComparison; }

    public BigDecimal getPreviousPeriodSales() { return previousPeriodSales; }
    public void setPreviousPeriodSales(BigDecimal previousPeriodSales) { this.previousPeriodSales = previousPeriodSales; }
}