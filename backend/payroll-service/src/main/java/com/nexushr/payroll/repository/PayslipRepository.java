package com.nexushr.payroll.repository;

import com.nexushr.payroll.model.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, String> {
    List<Payslip> findByPayrollRunId(String runId);
    List<Payslip> findByEmployeeIdOrderByCreatedAtDesc(String employeeId);
    List<Payslip> findByEmployeeIdAndPayrollRunId(String employeeId, String runId);
}
