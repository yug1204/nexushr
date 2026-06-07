package com.nexushr.payroll.service;

import com.nexushr.common.exception.BusinessException;
import com.nexushr.payroll.model.PayrollRun;
import com.nexushr.payroll.model.Payslip;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Slf4j
@Service
public class ExcelReportService {

    public byte[] generatePayrollJournal(PayrollRun run, List<Payslip> payslips) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Payroll Journal - " + run.getPeriodMonth() + "-" + run.getPeriodYear());

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Employee ID", "Employee Name", "Department", "Basic Salary", "HRA",
                    "Transport Allowance", "Special Allowance", "Gross Salary",
                    "PF Employee", "PF Employer", "ESI Employee", "ESI Employer",
                    "Professional Tax", "TDS", "Total Deductions", "Net Salary"
            };

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Populate Data
            int rowNum = 1;
            for (Payslip ps : payslips) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(ps.getEmployeeCode());
                row.createCell(1).setCellValue(ps.getEmployeeName());
                row.createCell(2).setCellValue(ps.getDepartment() != null ? ps.getDepartment() : "");
                row.createCell(3).setCellValue(ps.getBasicSalary().doubleValue());
                row.createCell(4).setCellValue(ps.getHra().doubleValue());
                row.createCell(5).setCellValue(ps.getTransportAllowance().doubleValue());
                row.createCell(6).setCellValue(ps.getSpecialAllowance().doubleValue());
                row.createCell(7).setCellValue(ps.getGrossSalary().doubleValue());
                row.createCell(8).setCellValue(ps.getPfEmployee().doubleValue());
                row.createCell(9).setCellValue(ps.getPfEmployer().doubleValue());
                row.createCell(10).setCellValue(ps.getEsiEmployee().doubleValue());
                row.createCell(11).setCellValue(ps.getEsiEmployer().doubleValue());
                row.createCell(12).setCellValue(ps.getProfessionalTax().doubleValue());
                row.createCell(13).setCellValue(ps.getTds().doubleValue());
                row.createCell(14).setCellValue(ps.getTotalDeductions().doubleValue());
                row.createCell(15).setCellValue(ps.getNetSalary().doubleValue());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Failed to generate Excel journal for run {}", run.getId(), e);
            throw new BusinessException("Failed to generate Payroll Journal Excel");
        }
    }
}
