package com.nexushr.payroll.model;

import com.nexushr.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payslips", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "payroll_run_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payslip extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "employee_code")
    private String employeeCode;

    @Column(name = "payroll_run_id", nullable = false)
    private String payrollRunId;

    // Earnings
    @Column(name = "basic_salary", precision = 15, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "hra", precision = 15, scale = 2)
    private BigDecimal hra;

    @Column(name = "transport_allowance", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal transportAllowance = BigDecimal.ZERO;

    @Column(name = "special_allowance", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal specialAllowance = BigDecimal.ZERO;

    @Column(name = "overtime_pay", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal overtimePay = BigDecimal.ZERO;

    @Column(name = "bonus", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal bonus = BigDecimal.ZERO;

    @Column(name = "gross_salary", precision = 15, scale = 2)
    private BigDecimal grossSalary;

    // Deductions
    @Column(name = "pf_employee", precision = 15, scale = 2)
    private BigDecimal pfEmployee;

    @Column(name = "pf_employer", precision = 15, scale = 2)
    private BigDecimal pfEmployer;

    @Column(name = "esi_employee", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal esiEmployee = BigDecimal.ZERO;

    @Column(name = "esi_employer", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal esiEmployer = BigDecimal.ZERO;

    @Column(name = "professional_tax", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal professionalTax = BigDecimal.ZERO;

    @Column(name = "tds", precision = 15, scale = 2)
    private BigDecimal tds;

    @Column(name = "total_deductions", precision = 15, scale = 2)
    private BigDecimal totalDeductions;

    @Column(name = "net_salary", precision = 15, scale = 2)
    private BigDecimal netSalary;

    // Tax Regime
    @Column(name = "tax_regime")
    private String taxRegime;

    // PDF
    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "designation")
    private String designation;

    @Column(name = "department")
    private String department;

    @Column(name = "bank_account")
    private String bankAccount;
}
