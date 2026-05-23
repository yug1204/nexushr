package com.nexushr.payroll.controller;

import com.nexushr.payroll.model.PayrollRun;
import com.nexushr.payroll.model.Payslip;
import com.nexushr.payroll.service.PayrollService;
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
}
