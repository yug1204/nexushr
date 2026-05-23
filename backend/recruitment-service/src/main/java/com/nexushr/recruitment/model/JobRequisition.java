package com.nexushr.recruitment.model;

import com.nexushr.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Job Requisition — the starting point of the recruitment pipeline.
 * Created by hiring manager, approved by HR, drives the ATS workflow.
 */
@Entity
@Table(name = "job_requisitions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JobRequisition extends AuditableEntity {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Column(name = "location", length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", length = 30)
    private EmploymentType employmentType;

    @Column(name = "min_experience")
    private Integer minExperience;

    @Column(name = "max_experience")
    private Integer maxExperience;

    @Column(name = "min_salary")
    private Long minSalary;

    @Column(name = "max_salary")
    private Long maxSalary;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills;

    @Column(name = "hiring_manager_id", length = 100)
    private String hiringManagerId;

    @Column(name = "number_of_openings")
    private Integer numberOfOpenings;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private RequisitionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private Priority priority;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDate approvedAt;

    public enum EmploymentType {
        FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP
    }

    public enum RequisitionStatus {
        DRAFT, PENDING_APPROVAL, APPROVED, OPEN, ON_HOLD, FILLED, CANCELLED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }
}
