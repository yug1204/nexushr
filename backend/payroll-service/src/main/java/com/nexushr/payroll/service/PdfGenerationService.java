package com.nexushr.payroll.service;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.nexushr.common.exception.BusinessException;
import com.nexushr.payroll.model.Payslip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Slf4j
@Service
public class PdfGenerationService {

    public byte[] generatePayslipPdf(Payslip payslip) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Header
            Paragraph header = new Paragraph("NexusHR Enterprise")
                    .setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(new DeviceRgb(79, 70, 229));
            document.add(header);

            document.add(new Paragraph("Payslip for the month").setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            // Employee Details Table
            Table empTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25})).useAllAvailableWidth();
            empTable.addCell(createCell("Employee ID:", true));
            empTable.addCell(createCell(payslip.getEmployeeCode(), false));
            empTable.addCell(createCell("Name:", true));
            empTable.addCell(createCell(payslip.getEmployeeName(), false));

            empTable.addCell(createCell("Department:", true));
            empTable.addCell(createCell(payslip.getDepartment() != null ? payslip.getDepartment() : "N/A", false));
            empTable.addCell(createCell("Designation:", true));
            empTable.addCell(createCell(payslip.getDesignation() != null ? payslip.getDesignation() : "N/A", false));
            document.add(empTable);
            document.add(new Paragraph("\n"));

            // Salary Breakdown Table
            Table salaryTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
            
            // Earnings Header
            salaryTable.addCell(new Cell().add(new Paragraph("Earnings")).setBold().setBackgroundColor(new DeviceRgb(240, 240, 240)));
            salaryTable.addCell(new Cell().add(new Paragraph("Deductions")).setBold().setBackgroundColor(new DeviceRgb(240, 240, 240)));

            // Basic & PF
            salaryTable.addCell(createRowTable("Basic Salary", payslip.getBasicSalary().toString()));
            salaryTable.addCell(createRowTable("Provident Fund", payslip.getPfEmployee().toString()));

            // HRA & ESI
            salaryTable.addCell(createRowTable("House Rent Allowance", payslip.getHra().toString()));
            salaryTable.addCell(createRowTable("ESI", payslip.getEsiEmployee().toString()));

            // Special & Tax
            salaryTable.addCell(createRowTable("Special Allowance", payslip.getSpecialAllowance().toString()));
            salaryTable.addCell(createRowTable("Professional Tax", payslip.getProfessionalTax().toString()));

            // Transport & TDS
            salaryTable.addCell(createRowTable("Transport Allowance", payslip.getTransportAllowance().toString()));
            salaryTable.addCell(createRowTable("TDS", payslip.getTds().toString()));

            // Totals
            salaryTable.addCell(new Cell().add(new Paragraph("Gross Earnings: " + payslip.getGrossSalary())).setBold());
            salaryTable.addCell(new Cell().add(new Paragraph("Total Deductions: " + payslip.getTotalDeductions())).setBold());

            document.add(salaryTable);
            document.add(new Paragraph("\n"));

            // Net Pay
            Paragraph netPay = new Paragraph("Net Pay: " + payslip.getNetSalary() + " INR")
                    .setBold().setFontSize(14).setTextAlignment(TextAlignment.RIGHT);
            document.add(netPay);

            // Footer
            document.add(new Paragraph("\n\n\n"));
            Paragraph footer = new Paragraph("This is a computer-generated document and does not require a signature.")
                    .setFontSize(8).setTextAlignment(TextAlignment.CENTER).setFontColor(new DeviceRgb(150, 150, 150));
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Failed to generate PDF for payslip {}", payslip.getId(), e);
            throw new BusinessException("Failed to generate Payslip PDF");
        }
    }

    private Cell createCell(String text, boolean isBold) {
        Paragraph p = new Paragraph(text);
        if (isBold) p.setBold();
        return new Cell().add(p).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
    }

    private Cell createRowTable(String label, String amount) {
        Table t = new Table(UnitValue.createPercentArray(new float[]{70, 30})).useAllAvailableWidth();
        t.addCell(new Cell().add(new Paragraph(label)).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
        t.addCell(new Cell().add(new Paragraph(amount).setTextAlignment(TextAlignment.RIGHT)).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
        return new Cell().add(t);
    }
}
