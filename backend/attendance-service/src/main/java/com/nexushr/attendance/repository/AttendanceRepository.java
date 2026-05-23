package com.nexushr.attendance.repository;

import com.nexushr.attendance.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, String> {

    Optional<AttendanceRecord> findByEmployeeIdAndAttendanceDate(String employeeId, LocalDate date);

    List<AttendanceRecord> findByAttendanceDateAndTenantIdOrderByClockInTimeAsc(LocalDate date, String tenantId);

    List<AttendanceRecord> findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
            String employeeId, LocalDate from, LocalDate to);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.attendanceDate = :date AND a.tenantId = :tenantId AND a.status = :status")
    long countByDateAndStatus(@Param("date") LocalDate date, @Param("tenantId") String tenantId,
                              @Param("status") AttendanceRecord.AttendanceStatus status);

    @Query("""
        SELECT a.status, COUNT(a) FROM AttendanceRecord a
        WHERE a.attendanceDate = :date AND a.tenantId = :tenantId
        GROUP BY a.status
    """)
    List<Object[]> getDailyStatusSummary(@Param("date") LocalDate date, @Param("tenantId") String tenantId);

    @Query("""
        SELECT AVG(a.totalHoursWorked) FROM AttendanceRecord a
        WHERE a.employeeId = :empId
        AND a.attendanceDate BETWEEN :from AND :to
        AND a.status = 'PRESENT'
    """)
    Double getAverageWorkHours(@Param("empId") String empId,
                               @Param("from") LocalDate from, @Param("to") LocalDate to);
}
