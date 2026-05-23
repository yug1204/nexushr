package com.nexushr.employee.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class EmployeeDtos {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateEmployeeRequest {
        @NotBlank private String firstName;
        @NotBlank private String lastName;
        @Email @NotBlank private String email;
        private String phoneNumber;
        private LocalDate dateOfBirth;
        @NotNull private String gender;
        @NotNull private LocalDate hireDate;
        @NotNull private String employmentType;
        private String departmentId;
        private String designation;
        private String grade;
        private String managerId;
        private BigDecimal ctc;
        private BigDecimal basicSalary;
        private BigDecimal hra;
        private BigDecimal specialAllowance;
        private String panNumber;
        private String aadhaarNumber;
        private String bankAccountNumber;
        private String ifscCode;
        private String address;
        private String city;
        private String state;
        private String pincode;
        private String emergencyContactName;
        private String emergencyContactPhone;
        private String taxRegime;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateEmployeeRequest {
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String designation;
        private String grade;
        private String managerId;
        private String departmentId;
        private BigDecimal ctc;
        private BigDecimal basicSalary;
        private BigDecimal hra;
        private BigDecimal specialAllowance;
        private String address;
        private String city;
        private String state;
        private String pincode;
        private String taxRegime;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class EmployeeResponse {
        private String id;
        private String employeeCode;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private LocalDate dateOfBirth;
        private String gender;
        private LocalDate hireDate;
        private String status;
        private String employmentType;
        private String departmentId;
        private String departmentName;
        private String designation;
        private String grade;
        private String managerId;
        private String managerName;
        private BigDecimal ctc;
        private String profilePhotoUrl;
        private String taxRegime;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class OrgChartNode {
        private String id;
        private String name;
        private String designation;
        private String departmentId;
        private String profilePhotoUrl;
        private int level;
        private List<OrgChartNode> children;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class EmployeeStats {
        private long totalEmployees;
        private long activeEmployees;
        private long newHiresThisMonth;
        private long terminationsThisMonth;
        private java.util.Map<String, Long> departmentDistribution;
        private java.util.Map<String, Long> genderDistribution;
    }
}
