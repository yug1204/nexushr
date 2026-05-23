package com.nexushr.payroll.engine;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Indian Tax Calculation Engine supporting Old and New tax regimes.
 * Handles TDS slab computation with rebate rules (Section 87A).
 */
@Slf4j
@Component
public class TaxCalculationEngine {

    /**
     * Calculate monthly TDS based on annual income and regime.
     */
    public BigDecimal calculateMonthlyTds(BigDecimal annualGross, String regime) {
        BigDecimal annualTax;
        if ("OLD".equalsIgnoreCase(regime)) {
            annualTax = calculateOldRegimeTax(annualGross);
        } else {
            annualTax = calculateNewRegimeTax(annualGross);
        }
        // Add 4% Health & Education Cess
        BigDecimal cess = annualTax.multiply(new BigDecimal("0.04"));
        BigDecimal totalTax = annualTax.add(cess);
        // Monthly TDS
        return totalTax.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
    }

    /**
     * New Tax Regime (FY 2025-26) slabs:
     * 0 - 4,00,000: Nil
     * 4,00,001 - 8,00,000: 5%
     * 8,00,001 - 12,00,000: 10%
     * 12,00,001 - 16,00,000: 15%
     * 16,00,001 - 20,00,000: 20%
     * 20,00,001 - 24,00,000: 25%
     * Above 24,00,000: 30%
     * Rebate: No tax if income <= 12,75,000 (Section 87A)
     */
    private BigDecimal calculateNewRegimeTax(BigDecimal income) {
        BigDecimal stdDeduction = new BigDecimal("75000");
        BigDecimal taxableIncome = income.subtract(stdDeduction);

        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;

        // Rebate under Section 87A
        if (taxableIncome.compareTo(new BigDecimal("1275000")) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal remaining = taxableIncome;

        BigDecimal[][] slabs = {
            {new BigDecimal("400000"), new BigDecimal("0")},
            {new BigDecimal("400000"), new BigDecimal("0.05")},
            {new BigDecimal("400000"), new BigDecimal("0.10")},
            {new BigDecimal("400000"), new BigDecimal("0.15")},
            {new BigDecimal("400000"), new BigDecimal("0.20")},
            {new BigDecimal("400000"), new BigDecimal("0.25")},
            {BigDecimal.valueOf(Long.MAX_VALUE), new BigDecimal("0.30")}
        };

        for (BigDecimal[] slab : slabs) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal taxable = remaining.min(slab[0]);
            tax = tax.add(taxable.multiply(slab[1]));
            remaining = remaining.subtract(taxable);
        }

        return tax.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Old Tax Regime slabs:
     * 0 - 2,50,000: Nil
     * 2,50,001 - 5,00,000: 5%
     * 5,00,001 - 10,00,000: 20%
     * Above 10,00,000: 30%
     */
    private BigDecimal calculateOldRegimeTax(BigDecimal income) {
        BigDecimal stdDeduction = new BigDecimal("50000");
        BigDecimal taxableIncome = income.subtract(stdDeduction);

        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        if (taxableIncome.compareTo(new BigDecimal("500000")) <= 0) return BigDecimal.ZERO; // 87A

        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal remaining = taxableIncome;

        BigDecimal[][] slabs = {
            {new BigDecimal("250000"), BigDecimal.ZERO},
            {new BigDecimal("250000"), new BigDecimal("0.05")},
            {new BigDecimal("500000"), new BigDecimal("0.20")},
            {BigDecimal.valueOf(Long.MAX_VALUE), new BigDecimal("0.30")}
        };

        for (BigDecimal[] slab : slabs) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal taxable = remaining.min(slab[0]);
            tax = tax.add(taxable.multiply(slab[1]));
            remaining = remaining.subtract(taxable);
        }

        return tax.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate PF: Employee 12% of Basic (capped at ₹15,000 basic for statutory)
     */
    public BigDecimal calculateEmployeePf(BigDecimal basicSalary) {
        BigDecimal pfBase = basicSalary.min(new BigDecimal("15000"));
        return pfBase.multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateEmployerPf(BigDecimal basicSalary) {
        BigDecimal pfBase = basicSalary.min(new BigDecimal("15000"));
        return pfBase.multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * ESI: Employee 0.75%, Employer 3.25% (applicable if gross <= ₹21,000/month)
     */
    public BigDecimal calculateEmployeeEsi(BigDecimal grossSalary) {
        if (grossSalary.compareTo(new BigDecimal("21000")) > 0) return BigDecimal.ZERO;
        return grossSalary.multiply(new BigDecimal("0.0075")).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateEmployerEsi(BigDecimal grossSalary) {
        if (grossSalary.compareTo(new BigDecimal("21000")) > 0) return BigDecimal.ZERO;
        return grossSalary.multiply(new BigDecimal("0.0325")).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Professional Tax (varies by state, using Maharashtra rates)
     */
    public BigDecimal calculateProfessionalTax(BigDecimal grossSalary) {
        if (grossSalary.compareTo(new BigDecimal("10000")) <= 0) return BigDecimal.ZERO;
        return new BigDecimal("200");
    }
}
