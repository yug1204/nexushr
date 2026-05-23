package com.nexushr.payroll.model;

import com.nexushr.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "payroll_runs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayrollRun extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "run_code", unique = true, nullable = false)
    private String runCode;

    @Column(name = "period_month", nullable = false)
    private int periodMonth;

    @Column(name = "period_year", nullable = false)
    private int periodYear;

    @Column(name = "run_date", nullable = false)
    private LocalDate runDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PayrollStatus status = PayrollStatus.DRAFT;

    @Column(name = "total_employees")
    private int totalEmployees;

    @Column(name = "total_gross", precision = 18, scale = 2)
    private BigDecimal totalGross;

    @Column(name = "total_deductions", precision = 18, scale = 2)
    private BigDecimal totalDeductions;

    @Column(name = "total_net", precision = 18, scale = 2)
    private BigDecimal totalNet;

    @Column(name = "total_employer_pf", precision = 18, scale = 2)
    private BigDecimal totalEmployerPf;

    @Column(name = "total_employer_esi", precision = 18, scale = 2)
    private BigDecimal totalEmployerEsi;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_at")
    private java.time.LocalDateTime approvedAt;

    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;

    public enum PayrollStatus { DRAFT, PROCESSING, CALCULATED, APPROVED, FINALIZED, FAILED }
}
