package com.nexushr.payroll.controller;

import com.nexushr.payroll.model.PayrollRun;
import com.nexushr.payroll.model.Payslip;
import com.nexushr.payroll.service.PayrollService;
import com.nexushr.payroll.service.PdfGenerationService;
import com.nexushr.payroll.service.ExcelReportService;
import com.nexushr.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
@Tag(name = "Payroll", description = "Payroll processing, payslips, and reports")
public class PayrollController {

    private final PayrollService payrollService;
    private final PdfGenerationService pdfGenerationService;
    private final ExcelReportService excelReportService;

    @PostMapping("/runs")
    @Operation(summary = "Initiate a new payroll run")
    public ResponseEntity<ApiResponse<PayrollRun>> initiate(
            @RequestParam int month, @RequestParam int year,
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "default") String tenantId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(payrollService.initiatePayrollRun(month, year, tenantId)));
    }

    @PostMapping("/runs/{runId}/process")
    @Operation(summary = "Process payroll calculations for all employees")
    public ResponseEntity<ApiResponse<PayrollRun>> process(
            @PathVariable String runId,
            @RequestBody List<PayrollService.EmployeePayrollData> employees) {
        return ResponseEntity.ok(ApiResponse.success(payrollService.processPayroll(runId, employees)));
    }

    @PostMapping("/runs/{runId}/approve")
    @Operation(summary = "Approve a calculated payroll run")
    public ResponseEntity<ApiResponse<PayrollRun>> approve(
            @PathVariable String runId, @RequestParam String approvedBy) {
        return ResponseEntity.ok(ApiResponse.success(payrollService.approvePayroll(runId, approvedBy)));
    }

    @GetMapping("/runs/{runId}/payslips")
    @Operation(summary = "Get all payslips for a payroll run")
    public ResponseEntity<ApiResponse<List<Payslip>>> payslipsByRun(@PathVariable String runId) {
        return ResponseEntity.ok(ApiResponse.success(payrollService.getPayslipsByRun(runId)));
    }

    @GetMapping("/employees/{employeeId}/payslips")
    @Operation(summary = "Get payslip history for an employee")
    public ResponseEntity<ApiResponse<List<Payslip>>> payslipsByEmployee(@PathVariable String employeeId) {
        return ResponseEntity.ok(ApiResponse.success(payrollService.getPayslipsByEmployee(employeeId)));
    }

    @GetMapping(value = "/payslips/{payslipId}/pdf", produces = "application/pdf")
    @Operation(summary = "Download Payslip as PDF")
    public ResponseEntity<byte[]> downloadPayslipPdf(@PathVariable String payslipId) {
        // In a real app, we fetch the payslip from DB
        Payslip mockPayslip = Payslip.builder()
                .employeeCode("EMP-" + payslipId.substring(0, Math.min(4, payslipId.length())))
                .employeeName("Jane Doe")
                .basicSalary(new java.math.BigDecimal("50000"))
                .hra(new java.math.BigDecimal("20000"))
                .transportAllowance(new java.math.BigDecimal("1600"))
                .specialAllowance(new java.math.BigDecimal("8400"))
                .grossSalary(new java.math.BigDecimal("80000"))
                .pfEmployee(new java.math.BigDecimal("1800"))
                .esiEmployee(new java.math.BigDecimal("600"))
                .professionalTax(new java.math.BigDecimal("200"))
                .tds(new java.math.BigDecimal("5000"))
                .totalDeductions(new java.math.BigDecimal("7600"))
                .netSalary(new java.math.BigDecimal("72400"))
                .build();
        
        byte[] pdfBytes = pdfGenerationService.generatePayslipPdf(mockPayslip);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"payslip-" + payslipId + ".pdf\"")
                .body(pdfBytes);
    }

    @GetMapping(value = "/runs/{runId}/journal", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(summary = "Download Payroll Journal as Excel")
    public ResponseEntity<byte[]> downloadPayrollJournal(@PathVariable String runId) {
        PayrollRun run = new PayrollRun();
        run.setId(runId);
        run.setPeriodMonth(5);
        run.setPeriodYear(2026);
        
        List<Payslip> payslips = payrollService.getPayslipsByRun(runId);
        byte[] excelBytes = excelReportService.generatePayrollJournal(run, payslips);
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"payroll-journal-" + runId + ".xlsx\"")
                .body(excelBytes);
    }
}
