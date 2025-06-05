package com.example.billingservice.service;

import com.example.billingservice.dto.InvoiceReportDto;
import com.example.billingservice.dto.PaymentReportDto;
import com.example.billingservice.dto.SalesReportDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {
    // PDF Reports
    byte[] generateInvoicePdf(Long invoiceId);
    byte[] generatePaymentReceiptPdf(Long paymentId);
    byte[] generateSalesReportPdf(LocalDateTime startDate, LocalDateTime endDate);
    byte[] generateOutstandingInvoicesPdf();

    // Excel Reports
    byte[] generateInvoicesExcel(LocalDateTime startDate, LocalDateTime endDate);
    byte[] generatePaymentsExcel(LocalDateTime startDate, LocalDateTime endDate);
    byte[] generateSalesAnalysisExcel(LocalDateTime startDate, LocalDateTime endDate);

    // Data Reports
    SalesReportDto generateSalesReport(LocalDateTime startDate, LocalDateTime endDate);
    List<InvoiceReportDto> generateOverdueReport();
    List<PaymentReportDto> generateCollectionReport(LocalDateTime startDate, LocalDateTime endDate);

    // Email Reports
    void emailInvoice(Long invoiceId, String toEmail);
    void emailPaymentReceipt(Long paymentId, String toEmail);
    void emailMonthlyReport(String toEmail, int year, int month);
}