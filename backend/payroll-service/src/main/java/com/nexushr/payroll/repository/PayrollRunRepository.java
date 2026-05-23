package com.nexushr.payroll.repository;

import com.nexushr.payroll.model.PayrollRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRunRepository extends JpaRepository<PayrollRun, String> {
    boolean existsByIdempotencyKey(String key);
    Optional<PayrollRun> findByRunCode(String runCode);
    List<PayrollRun> findByPeriodYearAndPeriodMonthOrderByRunDateDesc(int year, int month);
    List<PayrollRun> findByStatusOrderByRunDateDesc(PayrollRun.PayrollStatus status);
}
