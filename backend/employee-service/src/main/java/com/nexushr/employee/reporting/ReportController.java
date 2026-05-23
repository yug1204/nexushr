package com.nexushr.employee.reporting;

import com.nexushr.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * F-10: Report Builder & Analytics.
 * Provides pre-built and custom reports for HR decision-making.
 * Reports are generated from aggregate queries across the employee domain.
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reporting & Analytics", description = "Pre-built reports, custom queries, data export")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/headcount")
    @Operation(summary = "Headcount report by department, grade, gender")
    public ResponseEntity<ApiResponse<Map<String, Object>>> headcountReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.generateHeadcountReport()));
    }

    @GetMapping("/attrition")
    @Operation(summary = "Attrition trend report (monthly/quarterly)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> attritionReport(
            @RequestParam(defaultValue = "MONTHLY") String period) {
        return ResponseEntity.ok(ApiResponse.success(reportService.generateAttritionReport(period)));
    }

    @GetMapping("/payroll-summary")
    @Operation(summary = "Payroll cost summary by department")
    public ResponseEntity<ApiResponse<Map<String, Object>>> payrollSummary(
            @RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(ApiResponse.success(reportService.generatePayrollSummary(month, year)));
    }

    @GetMapping("/leave-analytics")
    @Operation(summary = "Leave utilization analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> leaveAnalytics(
            @RequestParam(defaultValue = "2026") int year) {
        return ResponseEntity.ok(ApiResponse.success(reportService.generateLeaveAnalytics(year)));
    }

    @GetMapping("/diversity")
    @Operation(summary = "Diversity & inclusion metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> diversityReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.generateDiversityReport()));
    }

    @GetMapping("/compensation-benchmarking")
    @Operation(summary = "Compensation benchmarking by grade and department")
    public ResponseEntity<ApiResponse<Map<String, Object>>> compensationReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.generateCompensationReport()));
    }

    @GetMapping("/executive-dashboard")
    @Operation(summary = "C-suite executive dashboard with all KPIs")
    public ResponseEntity<ApiResponse<Map<String, Object>>> executiveDashboard() {
        return ResponseEntity.ok(ApiResponse.success(reportService.generateExecutiveDashboard()));
    }
}
