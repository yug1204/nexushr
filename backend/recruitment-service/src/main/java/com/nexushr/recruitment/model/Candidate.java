package com.nexushr.recruitment.model;

import com.nexushr.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Candidate in the ATS pipeline.
 * Tracks the full journey from application to offer/rejection.
 */
@Entity
@Table(name = "candidates", indexes = {
    @Index(name = "idx_candidate_email", columnList = "email", unique = true),
    @Index(name = "idx_candidate_status", columnList = "pipeline_stage")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Candidate extends AuditableEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 200)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "resume_url", length = 500)
    private String resumeUrl;

    @Column(name = "linkedin_url", length = 300)
    private String linkedinUrl;

    @Column(name = "current_company", length = 200)
    private String currentCompany;

    @Column(name = "current_designation", length = 200)
    private String currentDesignation;

    @Column(name = "total_experience")
    private Integer totalExperience;

    @Column(name = "current_ctc")
    private Long currentCtc;

    @Column(name = "expected_ctc")
    private Long expectedCtc;

    @Column(name = "notice_period_days")
    private Integer noticePeriodDays;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_id")
    private JobRequisition requisition;

    @Enumerated(EnumType.STRING)
    @Column(name = "pipeline_stage", length = 30)
    private PipelineStage pipelineStage;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 30)
    private Source source;

    @Column(name = "recruiter_notes", columnDefinition = "TEXT")
    private String recruiterNotes;

    @Column(name = "interview_score")
    private Integer interviewScore;

    @Column(name = "ai_match_score")
    private Integer aiMatchScore;

    @Column(name = "offer_date")
    private LocalDate offerDate;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    public enum PipelineStage {
        APPLIED, SCREENING, PHONE_SCREEN, TECHNICAL_ROUND,
        HIRING_MANAGER_ROUND, HR_ROUND, OFFER_PENDING,
        OFFER_SENT, OFFER_ACCEPTED, OFFER_DECLINED,
        JOINED, REJECTED, WITHDRAWN
    }

    public enum Source {
        CAREER_PAGE, LINKEDIN, NAUKRI, REFERRAL, CAMPUS, AGENCY
    }
}
