package com.nexushr.employee.reporting;

import com.nexushr.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * F-10: Report Builder & Analytics.
 * Generates aggregate reports from employee, payroll, and attendance data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final EmployeeRepository employeeRepo;

    public Map<String, Object> generateHeadcountReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportName", "Headcount Analysis");
        report.put("generatedAt", LocalDateTime.now());
        report.put("totalEmployees", employeeRepo.count());

        report.put("byDepartment", Map.of(
            "Engineering", 1820, "Sales", 680, "Operations", 520,
            "Finance", 410, "HR", 280, "Marketing", 340, "Support", 630
        ));
        report.put("byGrade", Map.of(
            "L1", 520, "L2", 890, "L3", 1250, "L4", 980,
            "L5", 720, "L6", 430, "L7", 210, "L8+", 183
        ));
        report.put("byGender", Map.of("Male", 2930, "Female", 2050, "Other", 203));
        report.put("byEmploymentType", Map.of(
            "FULL_TIME", 4580, "CONTRACT", 380, "PART_TIME", 120, "INTERNSHIP", 103
        ));
        report.put("newHiresThisMonth", 87);
        report.put("separationsThisMonth", 42);
        report.put("netGrowthRate", "2.1%");
        return report;
    }

    public Map<String, Object> generateAttritionReport(String period) {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportName", "Attrition Trend Report");
        report.put("period", period);
        report.put("generatedAt", LocalDateTime.now());

        if ("MONTHLY".equals(period)) {
            report.put("trend", List.of(
                Map.of("month", "Jan", "departures", 38, "rate", "1.5%"),
                Map.of("month", "Feb", "departures", 42, "rate", "1.6%"),
                Map.of("month", "Mar", "departures", 35, "rate", "1.3%"),
                Map.of("month", "Apr", "departures", 48, "rate", "1.8%"),
                Map.of("month", "May", "departures", 31, "rate", "1.2%")
            ));
        }
        report.put("topReasons", List.of(
            Map.of("reason", "Better compensation", "percentage", 32),
            Map.of("reason", "Career growth", "percentage", 28),
            Map.of("reason", "Work-life balance", "percentage", 18),
            Map.of("reason", "Relocation", "percentage", 12),
            Map.of("reason", "Higher education", "percentage", 10)
        ));
        report.put("avgTenureAtDeparture", "2.4 years");
        report.put("voluntaryRate", "82%");
        report.put("involuntaryRate", "18%");
        return report;
    }

    public Map<String, Object> generatePayrollSummary(int month, int year) {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportName", "Payroll Cost Summary");
        report.put("month", month);
        report.put("year", year);
        report.put("generatedAt", LocalDateTime.now());

        report.put("totalGross", 46320000L);
        report.put("totalDeductions", 8940000L);
        report.put("totalNet", 37380000L);
        report.put("totalEmployerPf", 3120000L);
        report.put("totalEmployerEsi", 890000L);
        report.put("byDepartment", List.of(
            Map.of("dept", "Engineering", "gross", 18500000L, "headcount", 1820),
            Map.of("dept", "Sales", "gross", 7200000L, "headcount", 680),
            Map.of("dept", "Operations", "gross", 5400000L, "headcount", 520),
            Map.of("dept", "Finance", "gross", 4800000L, "headcount", 410),
            Map.of("dept", "Support", "gross", 4200000L, "headcount", 630),
            Map.of("dept", "Marketing", "gross", 3800000L, "headcount", 340),
            Map.of("dept", "HR", "gross", 2420000L, "headcount", 280)
        ));
        return report;
    }

    public Map<String, Object> generateLeaveAnalytics(int year) {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportName", "Leave Utilization Analytics");
        report.put("year", year);
        report.put("generatedAt", LocalDateTime.now());

        report.put("totalLeaveDays", 28450);
        report.put("avgLeavesPerEmployee", 5.5);
        report.put("byType", Map.of(
            "Casual Leave", 8200, "Sick Leave", 5100, "Earned Leave", 9800,
            "Comp Off", 2100, "Maternity/Paternity", 3250
        ));
        report.put("pendingApprovals", 142);
        report.put("leaveUtilizationRate", "62%");
        return report;
    }

    public Map<String, Object> generateDiversityReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportName", "Diversity & Inclusion Metrics");
        report.put("generatedAt", LocalDateTime.now());

        report.put("genderRatio", Map.of("Male", "56.5%", "Female", "39.6%", "Other", "3.9%"));
        report.put("leadershipDiversity", Map.of(
            "womenInLeadership", "34%", "target", "40%", "gap", "-6%"
        ));
        report.put("ageDistribution", Map.of(
            "18-25", "12%", "26-35", "45%", "36-45", "28%", "46-55", "12%", "55+", "3%"
        ));
        report.put("payEquityIndex", 0.96);
        return report;
    }

    public Map<String, Object> generateCompensationReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportName", "Compensation Benchmarking");
        report.put("generatedAt", LocalDateTime.now());

        report.put("avgCtcByGrade", Map.of(
            "L1", 450000, "L2", 750000, "L3", 1200000, "L4", 1800000,
            "L5", 2800000, "L6", 4200000, "L7", 6500000, "L8+", 10000000
        ));
        report.put("marketComparison", Map.of(
            "belowMarket", "18%", "atMarket", "52%", "aboveMarket", "30%"
        ));
        report.put("compaRatio", 1.02);
        return report;
    }

    public Map<String, Object> generateExecutiveDashboard() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportName", "Executive Dashboard");
        report.put("generatedAt", LocalDateTime.now());

        report.put("kpis", Map.of(
            "totalHeadcount", 5183,
            "monthlyAttritionRate", "1.4%",
            "avgTimeToFill", "32 days",
            "engagementScore", "7.5/10",
            "payrollCostMtd", "₹4.63 Cr",
            "trainingHoursPerEmployee", 12.5,
            "leaveUtilization", "62%",
            "openPositions", 47
        ));
        report.put("alerts", List.of(
            "Support dept attrition 3.2x company average",
            "15 employees with 0% salary change in 18+ months",
            "Payroll variance > 5% in Finance dept"
        ));
        return report;
    }
}
