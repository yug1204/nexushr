package com.nexushr.employee.model;

import com.nexushr.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees", indexes = {
    @Index(name = "idx_emp_email", columnList = "email", unique = true),
    @Index(name = "idx_emp_code", columnList = "employee_code", unique = true),
    @Index(name = "idx_emp_dept", columnList = "department_id"),
    @Index(name = "idx_emp_manager", columnList = "manager_id"),
    @Index(name = "idx_emp_tenant", columnList = "tenant_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "employee_code", nullable = false, unique = true)
    private String employeeCode;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false)
    private EmploymentType employmentType;

    @Column(name = "department_id")
    private String departmentId;

    @Column(name = "designation")
    private String designation;

    @Column(name = "grade")
    private String grade;

    @Column(name = "manager_id")
    private String managerId;

    @Column(name = "ctc", precision = 15, scale = 2)
    private BigDecimal ctc;

    @Column(name = "basic_salary", precision = 15, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "hra", precision = 15, scale = 2)
    private BigDecimal hra;

    @Column(name = "special_allowance", precision = 15, scale = 2)
    private BigDecimal specialAllowance;

    @Column(name = "pan_number")
    private String panNumber;

    @Column(name = "aadhaar_number")
    private String aadhaarNumber;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "pf_number")
    private String pfNumber;

    @Column(name = "esi_number")
    private String esiNumber;

    @Column(name = "uan_number")
    private String uanNumber;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @Column(name = "tax_regime")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TaxRegime taxRegime = TaxRegime.NEW;

    public enum EmployeeStatus { ACTIVE, ON_NOTICE, TERMINATED, ON_LEAVE, SUSPENDED }
    public enum EmploymentType { FULL_TIME, PART_TIME, CONTRACT, INTERN }
    public enum Gender { MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY }
    public enum TaxRegime { OLD, NEW }

    public String getFullName() { return firstName + " " + lastName; }
}
