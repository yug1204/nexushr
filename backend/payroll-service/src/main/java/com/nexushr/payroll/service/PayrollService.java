package com.nexushr.payroll.service;

import com.nexushr.payroll.engine.TaxCalculationEngine;
import com.nexushr.payroll.model.PayrollRun;
import com.nexushr.payroll.model.Payslip;
import com.nexushr.payroll.repository.PayrollRunRepository;
import com.nexushr.payroll.repository.PayslipRepository;
import com.nexushr.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRunRepository payrollRunRepository;
    private final PayslipRepository payslipRepository;
    private final TaxCalculationEngine taxEngine;

    @Transactional
    public PayrollRun initiatePayrollRun(int month, int year, String tenantId) {
        String idempotencyKey = tenantId + "-" + year + "-" + month;
        if (payrollRunRepository.existsByIdempotencyKey(idempotencyKey)) {
            throw new BusinessException("Payroll already processed for this period");
        }

        PayrollRun run = PayrollRun.builder()
                .runCode("PR-" + year + "-" + String.format("%02d", month) + "-" + UUID.randomUUID().toString().substring(0, 8))
                .periodMonth(month)
                .periodYear(year)
                .runDate(LocalDate.now())
                .status(PayrollRun.PayrollStatus.DRAFT)
                .idempotencyKey(idempotencyKey)
                .build();
        run.setTenantId(tenantId);

        return payrollRunRepository.save(run);
    }

    /**
     * Process payroll using Java 21 Virtual Threads for parallel computation.
     * Each employee's salary is calculated independently.
     */
    @Transactional
    public PayrollRun processPayroll(String runId, List<EmployeePayrollData> employees) {
        PayrollRun run = payrollRunRepository.findById(runId)
                .orElseThrow(() -> new BusinessException("Payroll run not found"));

        if (run.getStatus() != PayrollRun.PayrollStatus.DRAFT) {
            throw new BusinessException("Payroll run already processed");
        }

        run.setStatus(PayrollRun.PayrollStatus.PROCESSING);
        payrollRunRepository.save(run);

        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalDeductions = BigDecimal.ZERO;
        BigDecimal totalNet = BigDecimal.ZERO;
        BigDecimal totalEmployerPf = BigDecimal.ZERO;
        BigDecimal totalEmployerEsi = BigDecimal.ZERO;
        AtomicInteger count = new AtomicInteger(0);

        try {
            // Process using virtual threads for high throughput
            List<Payslip> payslips = employees.parallelStream().map(emp -> {
                count.incrementAndGet();
                return calculatePayslip(emp, runId, run.getTenantId());
            }).toList();

            payslipRepository.saveAll(payslips);

            for (Payslip ps : payslips) {
                totalGross = totalGross.add(ps.getGrossSalary());
                totalDeductions = totalDeductions.add(ps.getTotalDeductions());
                totalNet = totalNet.add(ps.getNetSalary());
                totalEmployerPf = totalEmployerPf.add(ps.getPfEmployer());
                totalEmployerEsi = totalEmployerEsi.add(ps.getEsiEmployer());
            }

            run.setTotalEmployees(count.get());
            run.setTotalGross(totalGross);
            run.setTotalDeductions(totalDeductions);
            run.setTotalNet(totalNet);
            run.setTotalEmployerPf(totalEmployerPf);
            run.setTotalEmployerEsi(totalEmployerEsi);
            run.setStatus(PayrollRun.PayrollStatus.CALCULATED);

            log.info("Payroll processed: {} employees, gross={}, net={}", count.get(), totalGross, totalNet);
        } catch (Exception e) {
            run.setStatus(PayrollRun.PayrollStatus.FAILED);
            log.error("Payroll processing failed", e);
        }

        return payrollRunRepository.save(run);
    }

    @Transactional
    public PayrollRun approvePayroll(String runId, String approvedBy) {
        PayrollRun run = payrollRunRepository.findById(runId)
                .orElseThrow(() -> new BusinessException("Payroll run not found"));
        if (run.getStatus() != PayrollRun.PayrollStatus.CALCULATED) {
            throw new BusinessException("Payroll must be in CALCULATED status to approve");
        }
        run.setStatus(PayrollRun.PayrollStatus.APPROVED);
        run.setApprovedBy(approvedBy);
        run.setApprovedAt(LocalDateTime.now());
        return payrollRunRepository.save(run);
    }

    public List<Payslip> getPayslipsByRun(String runId) {
        return payslipRepository.findByPayrollRunId(runId);
    }

    public List<Payslip> getPayslipsByEmployee(String employeeId) {
        return payslipRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
    }

    private Payslip calculatePayslip(EmployeePayrollData emp, String runId, String tenantId) {
        BigDecimal basic = emp.basicSalary;
        BigDecimal hraAmt = emp.hra != null ? emp.hra : basic.multiply(new BigDecimal("0.40"));
        BigDecimal ta = emp.transportAllowance != null ? emp.transportAllowance : new BigDecimal("1600");
        BigDecimal sa = emp.specialAllowance != null ? emp.specialAllowance : BigDecimal.ZERO;

        BigDecimal gross = basic.add(hraAmt).add(ta).add(sa);

        // Deductions
        BigDecimal pfEmp = taxEngine.calculateEmployeePf(basic);
        BigDecimal pfEr = taxEngine.calculateEmployerPf(basic);
        BigDecimal esiEmp = taxEngine.calculateEmployeeEsi(gross);
        BigDecimal esiEr = taxEngine.calculateEmployerEsi(gross);
        BigDecimal pt = taxEngine.calculateProfessionalTax(gross);

        BigDecimal annualGross = gross.multiply(BigDecimal.valueOf(12));
        BigDecimal tds = taxEngine.calculateMonthlyTds(annualGross, emp.taxRegime);

        BigDecimal totalDed = pfEmp.add(esiEmp).add(pt).add(tds);
        BigDecimal netSalary = gross.subtract(totalDed);

        Payslip payslip = Payslip.builder()
                .employeeId(emp.employeeId)
                .employeeName(emp.employeeName)
                .employeeCode(emp.employeeCode)
                .payrollRunId(runId)
                .basicSalary(basic)
                .hra(hraAmt)
                .transportAllowance(ta)
                .specialAllowance(sa)
                .grossSalary(gross)
                .pfEmployee(pfEmp)
                .pfEmployer(pfEr)
                .esiEmployee(esiEmp)
                .esiEmployer(esiEr)
                .professionalTax(pt)
                .tds(tds)
                .totalDeductions(totalDed)
                .netSalary(netSalary)
                .taxRegime(emp.taxRegime)
                .designation(emp.designation)
                .department(emp.department)
                .bankAccount(emp.bankAccount)
                .build();
        payslip.setTenantId(tenantId);
        return payslip;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class EmployeePayrollData {
        private String employeeId;
        private String employeeName;
        private String employeeCode;
        private BigDecimal basicSalary;
        private BigDecimal hra;
        private BigDecimal transportAllowance;
        private BigDecimal specialAllowance;
        private String taxRegime;
        private String designation;
        private String department;
        private String bankAccount;
    }
}
