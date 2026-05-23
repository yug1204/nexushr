package com.nexushr.payroll.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("TaxCalculationEngine Unit Tests")
class TaxCalculationEngineTest {

    private TaxCalculationEngine taxEngine;

    @BeforeEach
    void setUp() {
        taxEngine = new TaxCalculationEngine();
    }

    // ========== New Regime Tests ==========

    @Test
    @DisplayName("New Regime: Zero tax for income <= 12.75L (Section 87A rebate)")
    void newRegime_BelowRebateThreshold() {
        BigDecimal annual = new BigDecimal("1275000");
        BigDecimal monthlyTds = taxEngine.calculateMonthlyTds(annual, "NEW");
        assertThat(monthlyTds).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("New Regime: Zero tax for income below standard deduction")
    void newRegime_BelowStdDeduction() {
        BigDecimal annual = new BigDecimal("75000");
        BigDecimal monthlyTds = taxEngine.calculateMonthlyTds(annual, "NEW");
        assertThat(monthlyTds).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("New Regime: Tax for 15L income (above rebate threshold)")
    void newRegime_AboveRebate() {
        BigDecimal annual = new BigDecimal("1500000");
        BigDecimal monthlyTds = taxEngine.calculateMonthlyTds(annual, "NEW");
        assertThat(monthlyTds).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("New Regime: Tax for 25L income (hits 30% slab)")
    void newRegime_HighIncome() {
        BigDecimal annual = new BigDecimal("2500000");
        BigDecimal monthlyTds = taxEngine.calculateMonthlyTds(annual, "NEW");
        assertThat(monthlyTds).isGreaterThan(BigDecimal.ZERO);
        // Verify it includes 4% cess
    }

    // ========== Old Regime Tests ==========

    @Test
    @DisplayName("Old Regime: Zero tax for income <= 5L (Section 87A)")
    void oldRegime_BelowRebate() {
        BigDecimal annual = new BigDecimal("500000");
        BigDecimal monthlyTds = taxEngine.calculateMonthlyTds(annual, "OLD");
        assertThat(monthlyTds).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Old Regime: Tax for 12L income")
    void oldRegime_MiddleIncome() {
        BigDecimal annual = new BigDecimal("1200000");
        BigDecimal monthlyTds = taxEngine.calculateMonthlyTds(annual, "OLD");
        assertThat(monthlyTds).isGreaterThan(BigDecimal.ZERO);
    }

    // ========== PF Tests ==========

    @Test
    @DisplayName("PF: 12% of basic capped at 15000")
    void pf_CappedAtStatutoryLimit() {
        BigDecimal basic = new BigDecimal("50000");
        BigDecimal pf = taxEngine.calculateEmployeePf(basic);
        // Capped at 15000 * 12% = 1800
        assertThat(pf).isEqualByComparingTo(new BigDecimal("1800.00"));
    }

    @Test
    @DisplayName("PF: 12% of basic when below cap")
    void pf_BelowCap() {
        BigDecimal basic = new BigDecimal("10000");
        BigDecimal pf = taxEngine.calculateEmployeePf(basic);
        assertThat(pf).isEqualByComparingTo(new BigDecimal("1200.00"));
    }

    @Test
    @DisplayName("PF: Employer PF equals employee PF")
    void pf_EmployerEqualsEmployee() {
        BigDecimal basic = new BigDecimal("20000");
        BigDecimal empPf = taxEngine.calculateEmployeePf(basic);
        BigDecimal erPf = taxEngine.calculateEmployerPf(basic);
        assertThat(empPf).isEqualByComparingTo(erPf);
    }

    // ========== ESI Tests ==========

    @Test
    @DisplayName("ESI: Applied when gross <= 21000")
    void esi_ApplicableBelowThreshold() {
        BigDecimal gross = new BigDecimal("20000");
        BigDecimal esi = taxEngine.calculateEmployeeEsi(gross);
        assertThat(esi).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("ESI: Zero when gross > 21000")
    void esi_NotApplicableAboveThreshold() {
        BigDecimal gross = new BigDecimal("25000");
        BigDecimal esi = taxEngine.calculateEmployeeEsi(gross);
        assertThat(esi).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("ESI: Employer rate is 3.25%")
    void esi_EmployerRate() {
        BigDecimal gross = new BigDecimal("20000");
        BigDecimal esi = taxEngine.calculateEmployerEsi(gross);
        assertThat(esi).isEqualByComparingTo(new BigDecimal("650.00"));
    }

    // ========== Professional Tax Tests ==========

    @Test
    @DisplayName("PT: Zero for salary <= 10000")
    void pt_ExemptBelowThreshold() {
        BigDecimal gross = new BigDecimal("10000");
        BigDecimal pt = taxEngine.calculateProfessionalTax(gross);
        assertThat(pt).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("PT: 200 for salary > 10000 (Maharashtra)")
    void pt_MaharashtraRate() {
        BigDecimal gross = new BigDecimal("50000");
        BigDecimal pt = taxEngine.calculateProfessionalTax(gross);
        assertThat(pt).isEqualByComparingTo(new BigDecimal("200"));
    }
}
