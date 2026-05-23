package com.nexushr.employee.controller;

import com.nexushr.employee.dto.EmployeeDtos.*;
import com.nexushr.employee.service.EmployeeService;
import com.nexushr.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Employee lifecycle management")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @Operation(summary = "Create new employee (onboarding)")
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(
            @Valid @RequestBody CreateEmployeeRequest request,
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "default") String tenantId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(employeeService.createEmployee(request, tenantId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployee(id)));
    }

    @GetMapping
    @Operation(summary = "List all employees (paginated)")
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.listEmployees(pageable)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search employees by name, email, or code")
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> search(
            @RequestParam String q, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.searchEmployees(q, pageable)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee details")
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(
            @PathVariable String id, @Valid @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.updateEmployee(id, request)));
    }

    @PostMapping("/{id}/terminate")
    @Operation(summary = "Terminate employee (offboarding)")
    public ResponseEntity<ApiResponse<Void>> terminate(@PathVariable String id) {
        employeeService.terminateEmployee(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Employee terminated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete employee")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        employeeService.softDeleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Employee deleted"));
    }
}
