package com.nexushr.employee.service;

import com.nexushr.employee.dto.EmployeeDtos.*;
import com.nexushr.employee.model.Employee;
import com.nexushr.employee.repository.EmployeeRepository;
import com.nexushr.common.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private static final AtomicLong counter = new AtomicLong(1000);

    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request, String tenantId) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Employee with email already exists");
        }

        String empCode = "NHR-" + String.format("%06d", counter.incrementAndGet());

        Employee employee = Employee.builder()
                .employeeCode(empCode)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(Employee.Gender.valueOf(request.getGender()))
                .hireDate(request.getHireDate())
                .status(Employee.EmployeeStatus.ACTIVE)
                .employmentType(Employee.EmploymentType.valueOf(request.getEmploymentType()))
                .departmentId(request.getDepartmentId())
                .designation(request.getDesignation())
                .grade(request.getGrade())
                .managerId(request.getManagerId())
                .ctc(request.getCtc())
                .basicSalary(request.getBasicSalary())
                .hra(request.getHra())
                .specialAllowance(request.getSpecialAllowance())
                .panNumber(request.getPanNumber())
                .aadhaarNumber(request.getAadhaarNumber())
                .bankAccountNumber(request.getBankAccountNumber())
                .ifscCode(request.getIfscCode())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .taxRegime(request.getTaxRegime() != null ?
                        Employee.TaxRegime.valueOf(request.getTaxRegime()) : Employee.TaxRegime.NEW)
                .build();
        employee.setTenantId(tenantId);

        employee = employeeRepository.save(employee);
        log.info("Employee created: {} ({})", employee.getFullName(), empCode);
        return toResponse(employee);
    }

    public EmployeeResponse getEmployee(String id) {
        Employee emp = employeeRepository.findById(id)
                .filter(e -> !e.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
        return toResponse(emp);
    }

    public Page<EmployeeResponse> listEmployees(Pageable pageable) {
        return employeeRepository.findByDeletedFalse(pageable).map(this::toResponse);
    }

    public Page<EmployeeResponse> searchEmployees(String query, Pageable pageable) {
        return employeeRepository.search(query, pageable).map(this::toResponse);
    }

    @Transactional
    public EmployeeResponse updateEmployee(String id, UpdateEmployeeRequest request) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));

        if (request.getFirstName() != null) emp.setFirstName(request.getFirstName());
        if (request.getLastName() != null) emp.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) emp.setPhoneNumber(request.getPhoneNumber());
        if (request.getDesignation() != null) emp.setDesignation(request.getDesignation());
        if (request.getGrade() != null) emp.setGrade(request.getGrade());
        if (request.getManagerId() != null) emp.setManagerId(request.getManagerId());
        if (request.getDepartmentId() != null) emp.setDepartmentId(request.getDepartmentId());
        if (request.getCtc() != null) emp.setCtc(request.getCtc());
        if (request.getBasicSalary() != null) emp.setBasicSalary(request.getBasicSalary());
        if (request.getHra() != null) emp.setHra(request.getHra());
        if (request.getSpecialAllowance() != null) emp.setSpecialAllowance(request.getSpecialAllowance());
        if (request.getTaxRegime() != null) emp.setTaxRegime(Employee.TaxRegime.valueOf(request.getTaxRegime()));

        emp = employeeRepository.save(emp);
        return toResponse(emp);
    }

    @Transactional
    public void terminateEmployee(String id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        emp.setStatus(Employee.EmployeeStatus.TERMINATED);
        emp.setTerminationDate(LocalDate.now());
        employeeRepository.save(emp);
        log.info("Employee terminated: {}", emp.getEmployeeCode());
    }

    @Transactional
    public void softDeleteEmployee(String id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        emp.setDeleted(true);
        employeeRepository.save(emp);
    }

    private EmployeeResponse toResponse(Employee emp) {
        return EmployeeResponse.builder()
                .id(emp.getId())
                .employeeCode(emp.getEmployeeCode())
                .firstName(emp.getFirstName())
                .lastName(emp.getLastName())
                .email(emp.getEmail())
                .phoneNumber(emp.getPhoneNumber())
                .dateOfBirth(emp.getDateOfBirth())
                .gender(emp.getGender().name())
                .hireDate(emp.getHireDate())
                .status(emp.getStatus().name())
                .employmentType(emp.getEmploymentType().name())
                .departmentId(emp.getDepartmentId())
                .designation(emp.getDesignation())
                .grade(emp.getGrade())
                .managerId(emp.getManagerId())
                .ctc(emp.getCtc())
                .profilePhotoUrl(emp.getProfilePhotoUrl())
                .taxRegime(emp.getTaxRegime().name())
                .build();
    }
}
