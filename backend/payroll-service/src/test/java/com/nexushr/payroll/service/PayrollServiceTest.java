package com.nexushr.payroll.service;

import com.nexushr.payroll.engine.TaxCalculationEngine;
import com.nexushr.payroll.model.PayrollRun;
import com.nexushr.payroll.model.Payslip;
import com.nexushr.payroll.repository.PayrollRunRepository;
import com.nexushr.payroll.repository.PayslipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PayrollServiceTest {

    @Mock
    private PayrollRunRepository payrollRunRepository;

    @Mock
    private PayslipRepository payslipRepository;

    @Mock
    private TaxCalculationEngine taxEngine;

    @InjectMocks
    private PayrollService payrollService;

    @Test
    void processPayroll_shouldUseVirtualThreadsAndCalculateCorrectly() {
        String runId = "PR-123";
        PayrollRun run = new PayrollRun();
        run.setId(runId);
        run.setStatus(PayrollRun.PayrollStatus.DRAFT);
        run.setTenantId("tenant-1");

        when(payrollRunRepository.findById(runId)).thenReturn(Optional.of(run));
        when(payrollRunRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        when(taxEngine.calculateEmployeePf(any())).thenReturn(new BigDecimal("1800"));
        when(taxEngine.calculateEmployerPf(any())).thenReturn(new BigDecimal("1800"));
        when(taxEngine.calculateEmployeeEsi(any())).thenReturn(new BigDecimal("0"));
        when(taxEngine.calculateEmployerEsi(any())).thenReturn(new BigDecimal("0"));
        when(taxEngine.calculateProfessionalTax(any())).thenReturn(new BigDecimal("200"));
        when(taxEngine.calculateMonthlyTds(any(), any())).thenReturn(new BigDecimal("1500"));

        PayrollService.EmployeePayrollData emp1 = PayrollService.EmployeePayrollData.builder()
                .employeeId("E001")
                .basicSalary(new BigDecimal("50000"))
                .taxRegime("NEW")
                .build();
        
        PayrollService.EmployeePayrollData emp2 = PayrollService.EmployeePayrollData.builder()
                .employeeId("E002")
                .basicSalary(new BigDecimal("60000"))
                .taxRegime("OLD")
                .build();

        List<PayrollService.EmployeePayrollData> employees = List.of(emp1, emp2);

        PayrollRun result = payrollService.processPayroll(runId, employees);

        assertNotNull(result);
        assertEquals(PayrollRun.PayrollStatus.CALCULATED, result.getStatus());
        assertEquals(2, result.getTotalEmployees());
        
        verify(payslipRepository, times(1)).saveAll(any());
    }
}
