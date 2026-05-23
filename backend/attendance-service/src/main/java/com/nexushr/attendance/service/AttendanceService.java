package com.nexushr.attendance.service;

import com.nexushr.attendance.model.*;
import com.nexushr.attendance.repository.*;
import com.nexushr.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final AttendanceRepository attendanceRepo;
    private final LeaveRequestRepository leaveRequestRepo;
    private final LeaveBalanceRepository leaveBalanceRepo;

    // ========== CLOCK IN/OUT ==========

    @Transactional
    public AttendanceRecord clockIn(String employeeId, String tenantId, String source, String ip) {
        LocalDate today = LocalDate.now();

        Optional<AttendanceRecord> existing = attendanceRepo.findByEmployeeIdAndAttendanceDate(employeeId, today);
        if (existing.isPresent() && existing.get().getClockInTime() != null) {
            throw new BusinessException("Already clocked in today");
        }

        AttendanceRecord record = existing.orElse(AttendanceRecord.builder()
                .employeeId(employeeId)
                .attendanceDate(today)
                .build());

        record.setTenantId(tenantId);
        record.setClockInTime(LocalDateTime.now());
        record.setStatus(AttendanceRecord.AttendanceStatus.PRESENT);
        record.setClockSource(AttendanceRecord.ClockSource.valueOf(source != null ? source : "WEB"));
        record.setClockInIp(ip);

        log.info("Clock-in: employee={}, time={}", employeeId, record.getClockInTime());
        return attendanceRepo.save(record);
    }

    @Transactional
    public AttendanceRecord clockOut(String employeeId, String ip) {
        LocalDate today = LocalDate.now();
        AttendanceRecord record = attendanceRepo.findByEmployeeIdAndAttendanceDate(employeeId, today)
                .orElseThrow(() -> new BusinessException("No clock-in record found for today"));

        if (record.getClockOutTime() != null) {
            throw new BusinessException("Already clocked out today");
        }

        record.setClockOutTime(LocalDateTime.now());
        record.setClockOutIp(ip);
        record.calculateHours();

        // Determine half-day
        if (record.getTotalHoursWorked() != null && record.getTotalHoursWorked() < 4.0) {
            record.setStatus(AttendanceRecord.AttendanceStatus.HALF_DAY);
        }

        log.info("Clock-out: employee={}, hours={}", employeeId, record.getTotalHoursWorked());
        return attendanceRepo.save(record);
    }

    // ========== ATTENDANCE QUERIES ==========

    public List<AttendanceRecord> getTodayAttendance(String tenantId) {
        return attendanceRepo.findByAttendanceDateAndTenantIdOrderByClockInTimeAsc(LocalDate.now(), tenantId);
    }

    public List<AttendanceRecord> getEmployeeHistory(String employeeId, LocalDate from, LocalDate to) {
        return attendanceRepo.findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(employeeId, from, to);
    }

    public Map<String, Object> getDailySummary(String tenantId) {
        LocalDate today = LocalDate.now();
        List<Object[]> summary = attendanceRepo.getDailyStatusSummary(today, tenantId);

        Map<String, Object> result = new HashMap<>();
        long total = 0;
        for (Object[] row : summary) {
            String status = ((AttendanceRecord.AttendanceStatus) row[0]).name();
            Long count = (Long) row[1];
            result.put(status.toLowerCase(), count);
            total += count;
        }
        result.put("total", total);
        result.put("date", today.toString());
        return result;
    }

    // ========== LEAVE MANAGEMENT ==========

    @Transactional
    public LeaveRequest applyLeave(LeaveRequest request) {
        // Validate dates
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException("Start date cannot be after end date");
        }
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Cannot apply leave for past dates");
        }

        // Calculate days
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        double effectiveDays = days;
        if (request.isHalfDayStart()) effectiveDays -= 0.5;
        if (request.isHalfDayEnd()) effectiveDays -= 0.5;
        request.setTotalDays((int) Math.ceil(effectiveDays));

        // Check balance
        int year = request.getStartDate().getYear();
        LeaveBalance balance = leaveBalanceRepo
                .findByEmployeeIdAndLeaveTypeAndYearAndDeletedFalse(
                        request.getEmployeeId(), request.getLeaveType(), year)
                .orElseThrow(() -> new BusinessException("No leave balance configured for " + request.getLeaveType()));

        if (balance.getAvailable() < effectiveDays) {
            throw new BusinessException(String.format("Insufficient %s balance. Available: %.1f, Requested: %.1f",
                    request.getLeaveType(), balance.getAvailable(), effectiveDays));
        }

        // Reserve balance
        balance.setPending(balance.getPending() + effectiveDays);
        leaveBalanceRepo.save(balance);

        request.setStatus(LeaveRequest.LeaveStatus.PENDING);
        log.info("Leave applied: employee={}, type={}, days={}", request.getEmployeeId(), request.getLeaveType(), effectiveDays);
        return leaveRequestRepo.save(request);
    }

    @Transactional
    public LeaveRequest approveLeave(String leaveId, String approverId, String approverName, String remarks) {
        LeaveRequest leave = leaveRequestRepo.findById(leaveId)
                .orElseThrow(() -> new BusinessException("Leave request not found"));

        if (leave.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new BusinessException("Only pending requests can be approved");
        }

        leave.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        leave.setApproverId(approverId);
        leave.setApproverName(approverName);
        leave.setApprovedAt(LocalDate.now());
        leave.setApproverRemarks(remarks);

        // Move from pending to used in balance
        int year = leave.getStartDate().getYear();
        LeaveBalance balance = leaveBalanceRepo
                .findByEmployeeIdAndLeaveTypeAndYearAndDeletedFalse(
                        leave.getEmployeeId(), leave.getLeaveType(), year)
                .orElseThrow();

        balance.setPending(balance.getPending() - leave.getTotalDays());
        balance.setUsed(balance.getUsed() + leave.getTotalDays());
        leaveBalanceRepo.save(balance);

        log.info("Leave approved: id={}, by={}", leaveId, approverId);
        return leaveRequestRepo.save(leave);
    }

    @Transactional
    public LeaveRequest rejectLeave(String leaveId, String approverId, String remarks) {
        LeaveRequest leave = leaveRequestRepo.findById(leaveId)
                .orElseThrow(() -> new BusinessException("Leave request not found"));

        if (leave.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new BusinessException("Only pending requests can be rejected");
        }

        leave.setStatus(LeaveRequest.LeaveStatus.REJECTED);
        leave.setApproverId(approverId);
        leave.setApproverRemarks(remarks);

        // Release pending balance
        int year = leave.getStartDate().getYear();
        leaveBalanceRepo.findByEmployeeIdAndLeaveTypeAndYearAndDeletedFalse(
                leave.getEmployeeId(), leave.getLeaveType(), year)
                .ifPresent(b -> {
                    b.setPending(b.getPending() - leave.getTotalDays());
                    leaveBalanceRepo.save(b);
                });

        return leaveRequestRepo.save(leave);
    }

    public List<LeaveRequest> getPendingLeaves(String tenantId) {
        return leaveRequestRepo.findByStatusAndTenantIdOrderByCreatedAtDesc(LeaveRequest.LeaveStatus.PENDING, tenantId);
    }

    public List<LeaveBalance> getEmployeeBalances(String employeeId, int year) {
        return leaveBalanceRepo.findByEmployeeIdAndYearAndDeletedFalse(employeeId, year);
    }

    // ========== LEAVE BALANCE INITIALIZATION ==========

    @Transactional
    public void initializeLeaveBalances(String employeeId, String tenantId, int year) {
        Map<LeaveRequest.LeaveType, Double> defaults = Map.of(
                LeaveRequest.LeaveType.CASUAL_LEAVE, 12.0,
                LeaveRequest.LeaveType.SICK_LEAVE, 12.0,
                LeaveRequest.LeaveType.EARNED_LEAVE, 15.0,
                LeaveRequest.LeaveType.COMPENSATORY_OFF, 0.0
        );

        defaults.forEach((type, entitlement) -> {
            if (leaveBalanceRepo.findByEmployeeIdAndLeaveTypeAndYearAndDeletedFalse(employeeId, type, year).isEmpty()) {
                LeaveBalance balance = LeaveBalance.builder()
                        .employeeId(employeeId)
                        .leaveType(type)
                        .year(year)
                        .totalEntitled(entitlement)
                        .build();
                balance.setTenantId(tenantId);
                leaveBalanceRepo.save(balance);
            }
        });
        log.info("Leave balances initialized: employee={}, year={}", employeeId, year);
    }
}
