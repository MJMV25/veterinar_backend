// ReportServiceImpl.java
package com.example.billingservice.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.example.billingservice.dto.InvoiceReportDto;
import com.example.billingservice.dto.PaymentReportDto;
import com.example.billingservice.dto.SalesReportDto;
import com.example.billingservice.entity.*;
import com.example.billingservice.exception.BillingException;
import com.example.billingservice.repository.InvoiceRepository;
import com.example.billingservice.repository.InvoiceItemRepository;
import com.example.billingservice.repository.PaymentRepository;
import com.example.billingservice.service.ReportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public byte[] generateInvoicePdf(Long invoiceId) {
        Optional<Invoice> invoiceOpt = invoiceRepository.findById(invoiceId);
        if (invoiceOpt.isEmpty()) {
            throw new BillingException("Invoice not found with ID: " + invoiceId);
        }

        Invoice invoice = invoiceOpt.get();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Header
            document.add(new Paragraph("VETERINARIA - FACTURA")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold());

            // Invoice details
            document.add(new Paragraph("\nFACTURA #: " + invoice.getInvoiceNumber())
                    .setFontSize(14)
                    .setBold());

            if (invoice.getIssueDate() != null) {
                document.add(new Paragraph("Fecha: " + invoice.getIssueDate().format(DATE_FORMATTER)));
            }
            if (invoice.getDueDate() != null) {
                document.add(new Paragraph("Fecha de Vencimiento: " + invoice.getDueDate().format(DATE_FORMATTER)));
            }

            // Client information
            document.add(new Paragraph("\nDATOS DEL CLIENTE:")
                    .setFontSize(12)
                    .setBold());
            document.add(new Paragraph("Nombre: " + (invoice.getClientName() != null ? invoice.getClientName() : "N/A")));
            if (invoice.getClientEmail() != null) {
                document.add(new Paragraph("Email: " + invoice.getClientEmail()));
            }
            if (invoice.getClientPhone() != null) {
                document.add(new Paragraph("Teléfono: " + invoice.getClientPhone()));
            }
            if (invoice.getClientAddress() != null) {
                document.add(new Paragraph("Dirección: " + invoice.getClientAddress()));
            }
            if (invoice.getPetName() != null) {
                document.add(new Paragraph("Mascota: " + invoice.getPetName()));
            }

            // Items table
            document.add(new Paragraph("\nDETALLE DE SERVICIOS:")
                    .setFontSize(12)
                    .setBold());

            Table table = new Table(new float[]{4, 1, 2, 2, 2});
            table.setWidth(UnitValue.createPercentValue(100));

            // Table headers - usando nombres completamente calificados
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Descripción")).setBold());
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Cant.")).setBold());
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Precio Unit.")).setBold());
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Descuento")).setBold());
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Total")).setBold());

            // Table rows
            if (invoice.getItems() != null) {
                for (InvoiceItem item : invoice.getItems()) {
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(item.getDescription())));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(item.getQuantity().toString())));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("$" + formatMoney(item.getUnitPrice()))));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("$" + formatMoney(item.getDiscountAmount()))));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("$" + formatMoney(item.getTotal()))));
                }
            }

            document.add(table);

            // Totals
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Subtotal: $" + formatMoney(invoice.getSubtotal()))
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("Descuento: $" + formatMoney(invoice.getDiscountAmount()))
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("IVA (" + invoice.getTaxPercentage() + "%): $" + formatMoney(invoice.getTaxAmount()))
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("TOTAL: $" + formatMoney(invoice.getTotalAmount()))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(14)
                    .setBold());

            // Payment info
            if (invoice.getPaidAmount() != null && invoice.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
                document.add(new Paragraph("Pagado: $" + formatMoney(invoice.getPaidAmount()))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setFontColor(ColorConstants.GREEN));
                document.add(new Paragraph("Saldo Pendiente: $" + formatMoney(invoice.getBalanceDue()))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setFontColor(invoice.getBalanceDue().compareTo(BigDecimal.ZERO) > 0 ?
                                ColorConstants.RED : ColorConstants.GREEN));
            }

            // Notes
            if (invoice.getNotes() != null && !invoice.getNotes().isEmpty()) {
                document.add(new Paragraph("\nNotas:")
                        .setFontSize(12)
                        .setBold());
                document.add(new Paragraph(invoice.getNotes()));
            }

            // Terms and conditions
            if (invoice.getTermsConditions() != null && !invoice.getTermsConditions().isEmpty()) {
                document.add(new Paragraph("\nTérminos y Condiciones:")
                        .setFontSize(10)
                        .setBold());
                document.add(new Paragraph(invoice.getTermsConditions())
                        .setFontSize(8));
            }

            document.close();

            logger.info("Generated PDF for invoice: {}", invoice.getInvoiceNumber());
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Error generating invoice PDF: {}", e.getMessage());
            throw new BillingException("Error generating invoice PDF", e);
        }
    }

    @Override
    public byte[] generatePaymentReceiptPdf(Long paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new BillingException("Payment not found with ID: " + paymentId);
        }

        Payment payment = paymentOpt.get();
        Invoice invoice = payment.getInvoice();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Header
            document.add(new Paragraph("VETERINARIA - RECIBO DE PAGO")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18)
                    .setBold());

            // Payment details
            document.add(new Paragraph("\nRECIBO #: " + payment.getPaymentNumber())
                    .setFontSize(14)
                    .setBold());
            document.add(new Paragraph("Fecha de Pago: " + payment.getPaymentDate().format(DATETIME_FORMATTER)));
            document.add(new Paragraph("Factura Relacionada: " + invoice.getInvoiceNumber()));

            // Client information
            document.add(new Paragraph("\nCLIENTE: " + invoice.getClientName())
                    .setFontSize(12)
                    .setBold());
            if (invoice.getPetName() != null) {
                document.add(new Paragraph("Mascota: " + invoice.getPetName()));
            }

            // Payment information
            document.add(new Paragraph("\nDETALLES DEL PAGO:")
                    .setFontSize(12)
                    .setBold());
            document.add(new Paragraph("Monto Pagado: $" + formatMoney(payment.getAmount()))
                    .setFontSize(14));
            document.add(new Paragraph("Método de Pago: " + payment.getPaymentMethod().getDescription()));

            if (payment.getReferenceNumber() != null) {
                document.add(new Paragraph("Número de Referencia: " + payment.getReferenceNumber()));
            }
            if (payment.getTransactionId() != null) {
                document.add(new Paragraph("ID de Transacción: " + payment.getTransactionId()));
            }

            // Invoice summary
            document.add(new Paragraph("\nRESUMEN DE FACTURA:")
                    .setFontSize(12)
                    .setBold());
            document.add(new Paragraph("Total de Factura: $" + formatMoney(invoice.getTotalAmount())));
            document.add(new Paragraph("Total Pagado: $" + formatMoney(invoice.getPaidAmount())));
            document.add(new Paragraph("Saldo Pendiente: $" + formatMoney(invoice.getBalanceDue()))
                    .setFontColor(invoice.getBalanceDue().compareTo(BigDecimal.ZERO) > 0 ?
                            ColorConstants.RED : ColorConstants.GREEN));

            if (payment.getNotes() != null && !payment.getNotes().isEmpty()) {
                document.add(new Paragraph("\nNotas: " + payment.getNotes()));
            }

            document.add(new Paragraph("\n\nGracias por su pago!")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12)
                    .setBold());

            document.close();

            logger.info("Generated payment receipt PDF for payment: {}", payment.getPaymentNumber());
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Error generating payment receipt PDF: {}", e.getMessage());
            throw new BillingException("Error generating payment receipt PDF", e);
        }
    }

    @Override
    public byte[] generateSalesReportPdf(LocalDateTime startDate, LocalDateTime endDate) {
        SalesReportDto salesReport = generateSalesReport(startDate, endDate);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Header
            document.add(new Paragraph("REPORTE DE VENTAS")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold());

            document.add(new Paragraph("Período: " + startDate.format(DATE_FORMATTER) + " - " + endDate.format(DATE_FORMATTER))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12));

            // Summary
            document.add(new Paragraph("\nRESUMEN EJECUTIVO:")
                    .setFontSize(14)
                    .setBold());

            document.add(new Paragraph("Total Ventas: $" + formatMoney(salesReport.getTotalSales())));
            document.add(new Paragraph("Total Cobrado: $" + formatMoney(salesReport.getTotalPaid())));
            document.add(new Paragraph("Pendiente de Cobro: $" + formatMoney(salesReport.getTotalOutstanding())));
            document.add(new Paragraph("Facturas Generadas: " + salesReport.getTotalInvoices()));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Error generating sales report PDF: {}", e.getMessage());
            throw new BillingException("Error generating sales report PDF", e);
        }
    }

    @Override
    public byte[] generateOutstandingInvoicesPdf() {
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(LocalDateTime.now());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("FACTURAS PENDIENTES")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold());

            Table table = new Table(new float[]{3, 2, 2, 2, 2});
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Cliente")).setBold());
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Factura")).setBold());
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Total")).setBold());
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Saldo")).setBold());
            table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Días Vencido")).setBold());

            for (Invoice invoice : overdueInvoices) {
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(invoice.getClientName())));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(invoice.getInvoiceNumber())));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("$" + formatMoney(invoice.getTotalAmount()))));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("$" + formatMoney(invoice.getBalanceDue()))));

                long daysOverdue = java.time.Duration.between(invoice.getDueDate(), LocalDateTime.now()).toDays();
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(daysOverdue))));
            }

            document.add(table);
            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Error generating outstanding invoices PDF: {}", e.getMessage());
            throw new BillingException("Error generating outstanding invoices PDF", e);
        }
    }

    @Override
    public byte[] generateInvoicesExcel(LocalDateTime startDate, LocalDateTime endDate) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Facturas");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Número", "Cliente", "Mascota", "Fecha", "Total", "Pagado", "Saldo", "Estado"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell excelCell = headerRow.createCell(i);
                excelCell.setCellValue(headers[i]);
                excelCell.setCellStyle(headerStyle);
            }

            // Get invoices
            Page<Invoice> invoices = invoiceRepository.findByIssueDateBetween(
                    startDate, endDate, PageRequest.of(0, 1000));

            int rowNum = 1;
            for (Invoice invoice : invoices.getContent()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(invoice.getInvoiceNumber());
                row.createCell(1).setCellValue(invoice.getClientName());
                row.createCell(2).setCellValue(invoice.getPetName() != null ? invoice.getPetName() : "");
                row.createCell(3).setCellValue(invoice.getIssueDate() != null ?
                        invoice.getIssueDate().format(DATE_FORMATTER) : "");
                row.createCell(4).setCellValue(invoice.getTotalAmount().doubleValue());
                row.createCell(5).setCellValue(invoice.getPaidAmount().doubleValue());
                row.createCell(6).setCellValue(invoice.getBalanceDue().doubleValue());
                row.createCell(7).setCellValue(invoice.getStatus().getDescription());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Error generating invoices Excel: {}", e.getMessage());
            throw new BillingException("Error generating invoices Excel", e);
        }
    }

    @Override
    public byte[] generatePaymentsExcel(LocalDateTime startDate, LocalDateTime endDate) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Pagos");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Número Pago", "Factura", "Cliente", "Monto", "Método", "Fecha", "Estado"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell excelCell = headerRow.createCell(i);
                excelCell.setCellValue(headers[i]);
                excelCell.setCellStyle(headerStyle);
            }

            Page<Payment> payments = paymentRepository.findByPaymentDateBetween(
                    startDate, endDate, PageRequest.of(0, 1000));

            int rowNum = 1;
            for (Payment payment : payments.getContent()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(payment.getPaymentNumber());
                row.createCell(1).setCellValue(payment.getInvoice().getInvoiceNumber());
                row.createCell(2).setCellValue(payment.getInvoice().getClientName());
                row.createCell(3).setCellValue(payment.getAmount().doubleValue());
                row.createCell(4).setCellValue(payment.getPaymentMethod().getDescription());
                row.createCell(5).setCellValue(payment.getPaymentDate().format(DATETIME_FORMATTER));
                row.createCell(6).setCellValue(payment.getPaymentStatus().getDescription());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Error generating payments Excel: {}", e.getMessage());
            throw new BillingException("Error generating payments Excel", e);
        }
    }

    @Override
    public byte[] generateSalesAnalysisExcel(LocalDateTime startDate, LocalDateTime endDate) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            SalesReportDto salesReport = generateSalesReport(startDate, endDate);

            // Summary sheet
            Sheet summarySheet = workbook.createSheet("Resumen");
            int rowNum = 0;
            summarySheet.createRow(rowNum++).createCell(0).setCellValue("RESUMEN DE VENTAS");
            summarySheet.createRow(rowNum++).createCell(0).setCellValue("Total Ventas: $" + formatMoney(salesReport.getTotalSales()));
            summarySheet.createRow(rowNum++).createCell(0).setCellValue("Total Cobrado: $" + formatMoney(salesReport.getTotalPaid()));
            summarySheet.createRow(rowNum++).createCell(0).setCellValue("Pendiente: $" + formatMoney(salesReport.getTotalOutstanding()));

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Error generating sales analysis Excel: {}", e.getMessage());
            throw new BillingException("Error generating sales analysis Excel", e);
        }
    }

    @Override
    public SalesReportDto generateSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        SalesReportDto report = new SalesReportDto(startDate, endDate, "CUSTOM");

        // Get basic statistics
        Page<Invoice> invoices = invoiceRepository.findByIssueDateBetween(
                startDate, endDate, PageRequest.of(0, Integer.MAX_VALUE));

        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalOutstanding = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        int totalInvoices = 0;
        int paidInvoices = 0;
        int pendingInvoices = 0;
        int overdueInvoices = 0;

        for (Invoice invoice : invoices.getContent()) {
            totalSales = totalSales.add(invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO);
            totalPaid = totalPaid.add(invoice.getPaidAmount() != null ? invoice.getPaidAmount() : BigDecimal.ZERO);
            totalOutstanding = totalOutstanding.add(invoice.getBalanceDue() != null ? invoice.getBalanceDue() : BigDecimal.ZERO);
            totalTax = totalTax.add(invoice.getTaxAmount() != null ? invoice.getTaxAmount() : BigDecimal.ZERO);
            totalDiscount = totalDiscount.add(invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : BigDecimal.ZERO);
            totalInvoices++;

            if (invoice.getPaymentStatus() != null) {
                switch (invoice.getPaymentStatus()) {
                    case PAID:
                        paidInvoices++;
                        break;
                    case PENDING:
                        pendingInvoices++;
                        break;
                    default:
                        if (invoice.getStatus() == InvoiceStatus.OVERDUE) {
                            overdueInvoices++;
                        }
                        break;
                }
            }
        }

        report.setTotalSales(totalSales);
        report.setTotalPaid(totalPaid);
        report.setTotalOutstanding(totalOutstanding);
        report.setTotalTax(totalTax);
        report.setTotalDiscount(totalDiscount);
        report.setTotalInvoices(totalInvoices);
        report.setPaidInvoices(paidInvoices);
        report.setPendingInvoices(pendingInvoices);
        report.setOverdueInvoices(overdueInvoices);

        return report;
    }

    @Override
    public List<InvoiceReportDto> generateOverdueReport() {
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(LocalDateTime.now());
        return overdueInvoices.stream()
                .map(this::mapToInvoiceReportDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentReportDto> generateCollectionReport(LocalDateTime startDate, LocalDateTime endDate) {
        Page<Payment> payments = paymentRepository.findByPaymentDateBetween(
                startDate, endDate, PageRequest.of(0, Integer.MAX_VALUE));

        return payments.getContent().stream()
                .map(this::mapToPaymentReportDto)
                .collect(Collectors.toList());
    }

    @Override
    public void emailInvoice(Long invoiceId, String toEmail) {
        try {
            byte[] pdfBytes = generateInvoicePdf(invoiceId);
            // Here you would integrate with an email service
            logger.info("Invoice {} emailed to {}", invoiceId, toEmail);
        } catch (Exception e) {
            logger.error("Error emailing invoice {}: {}", invoiceId, e.getMessage());
            throw new BillingException("Error emailing invoice", e);
        }
    }

    @Override
    public void emailPaymentReceipt(Long paymentId, String toEmail) {
        try {
            byte[] pdfBytes = generatePaymentReceiptPdf(paymentId);
            // Here you would integrate with an email service
            logger.info("Payment receipt {} emailed to {}", paymentId, toEmail);
        } catch (Exception e) {
            logger.error("Error emailing payment receipt {}: {}", paymentId, e.getMessage());
            throw new BillingException("Error emailing payment receipt", e);
        }
    }

    @Override
    public void emailMonthlyReport(String toEmail, int year, int month) {
        try {
            LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime endDate = startDate.plusMonths(1).minusDays(1);
            byte[] pdfBytes = generateSalesReportPdf(startDate, endDate);
            // Here you would integrate with an email service
            logger.info("Monthly report for {}/{} emailed to {}", month, year, toEmail);
        } catch (Exception e) {
            logger.error("Error emailing monthly report: {}", e.getMessage());
            throw new BillingException("Error emailing monthly report", e);
        }
    }

    // Helper methods
    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0.00";
        return String.format("%,.2f", amount);
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

        if (invoice.getItems() != null && !invoice.getItems().isEmpty()) {
            String servicesSummary = invoice.getItems().stream()
                    .map(item -> item.getDescription())
                    .collect(Collectors.joining(", "));
            dto.setServicesSummary(servicesSummary);
        }

        return dto;
    }

    private PaymentReportDto mapToPaymentReportDto(Payment payment) {
        PaymentReportDto dto = new PaymentReportDto();
        dto.setId(payment.getId());
        dto.setPaymentNumber(payment.getPaymentNumber());
        dto.setInvoiceId(payment.getInvoice().getId());
        dto.setInvoiceNumber(payment.getInvoice().getInvoiceNumber());
        dto.setClientName(payment.getInvoice().getClientName());
        dto.setPetName(payment.getInvoice().getPetName());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setReferenceNumber(payment.getReferenceNumber());
        dto.setTransactionId(payment.getTransactionId());
        dto.setProcessedByName(payment.getProcessedByName());
        dto.setNotes(payment.getNotes());
        dto.setInvoiceTotal(payment.getInvoice().getTotalAmount());
        dto.setRemainingBalance(payment.getInvoice().getBalanceDue());

        return dto;
    }
}