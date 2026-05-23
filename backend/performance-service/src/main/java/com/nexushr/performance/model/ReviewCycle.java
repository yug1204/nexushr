package com.nexushr.performance.model;

import com.nexushr.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "review_cycles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewCycle extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(name = "cycle_type", nullable = false)
    private CycleType cycleType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "goal_setting_deadline")
    private LocalDate goalSettingDeadline;

    @Column(name = "self_review_deadline")
    private LocalDate selfReviewDeadline;

    @Column(name = "manager_review_deadline")
    private LocalDate managerReviewDeadline;

    @Column(name = "calibration_deadline")
    private LocalDate calibrationDeadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CycleStatus status = CycleStatus.SETUP;

    public enum CycleType { MID_YEAR, ANNUAL, QUARTERLY }
    public enum CycleStatus { SETUP, GOAL_SETTING, SELF_REVIEW, MANAGER_REVIEW, CALIBRATION, PUBLISHED, CLOSED }
}
