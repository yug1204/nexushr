package com.nexushr.attendance.controller;

import com.nexushr.attendance.model.*;
import com.nexushr.attendance.service.AttendanceService;
import com.nexushr.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Clock-in/out, leave management, daily summaries")
public class AttendanceController {

    private final AttendanceService attendanceService;

    // ========== CLOCK IN/OUT ==========

    @PostMapping("/clock-in")
    @Operation(summary = "Clock in for the day")
    public ResponseEntity<ApiResponse<AttendanceRecord>> clockIn(
            @RequestParam String employeeId,
            @RequestParam(defaultValue = "WEB") String source,
            HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.clockIn(employeeId, "default", source, ip)));
    }

    @PostMapping("/clock-out")
    @Operation(summary = "Clock out for the day")
    public ResponseEntity<ApiResponse<AttendanceRecord>> clockOut(
            @RequestParam String employeeId,
            HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.clockOut(employeeId, request.getRemoteAddr())));
    }

    // ========== ATTENDANCE QUERIES ==========

    @GetMapping("/today")
    @Operation(summary = "Get today's attendance for all employees")
    public ResponseEntity<ApiResponse<List<AttendanceRecord>>> getTodayAttendance() {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getTodayAttendance("default")));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get daily attendance summary (present/absent/leave counts)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDailySummary() {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getDailySummary("default")));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get attendance history for an employee")
    public ResponseEntity<ApiResponse<List<AttendanceRecord>>> getHistory(
            @PathVariable String employeeId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getEmployeeHistory(employeeId, from, to)));
    }

    // ========== LEAVE MANAGEMENT ==========

    @PostMapping("/leave/apply")
    @Operation(summary = "Apply for leave")
    public ResponseEntity<ApiResponse<LeaveRequest>> applyLeave(@RequestBody LeaveRequest request) {
        request.setTenantId("default");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(attendanceService.applyLeave(request)));
    }

    @PutMapping("/leave/{id}/approve")
    @Operation(summary = "Approve a leave request")
    public ResponseEntity<ApiResponse<LeaveRequest>> approveLeave(
            @PathVariable String id,
            @RequestParam String approverId,
            @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.approveLeave(id, approverId, "HR Admin", remarks)));
    }

    @PutMapping("/leave/{id}/reject")
    @Operation(summary = "Reject a leave request")
    public ResponseEntity<ApiResponse<LeaveRequest>> rejectLeave(
            @PathVariable String id,
            @RequestParam String approverId,
            @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.rejectLeave(id, approverId, remarks)));
    }

    @GetMapping("/leave/pending")
    @Operation(summary = "Get all pending leave requests")
    public ResponseEntity<ApiResponse<List<LeaveRequest>>> getPendingLeaves() {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getPendingLeaves("default")));
    }

    @GetMapping("/leave/balance/{employeeId}")
    @Operation(summary = "Get leave balances for an employee")
    public ResponseEntity<ApiResponse<List<LeaveBalance>>> getBalances(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "2026") int year) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getEmployeeBalances(employeeId, year)));
    }

    @PostMapping("/leave/balance/init/{employeeId}")
    @Operation(summary = "Initialize leave balances for a new employee")
    public ResponseEntity<ApiResponse<String>> initBalances(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "2026") int year) {
        attendanceService.initializeLeaveBalances(employeeId, "default", year);
        return ResponseEntity.ok(ApiResponse.success("Leave balances initialized for " + employeeId));
    }
}
