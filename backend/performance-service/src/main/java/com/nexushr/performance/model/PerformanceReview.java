package com.nexushr.performance.model;

import com.nexushr.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "performance_reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PerformanceReview extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "review_cycle_id", nullable = false)
    private String reviewCycleId;

    @Column(name = "reviewee_id", nullable = false)
    private String revieweeId;

    @Column(name = "reviewer_id", nullable = false)
    private String reviewerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_type", nullable = false)
    private ReviewType reviewType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReviewStage stage = ReviewStage.DRAFT;

    @Column(name = "self_rating", precision = 3, scale = 1)
    private BigDecimal selfRating;

    @Column(name = "manager_rating", precision = 3, scale = 1)
    private BigDecimal managerRating;

    @Column(name = "final_rating", precision = 3, scale = 1)
    private BigDecimal finalRating;

    @Column(name = "self_comments", columnDefinition = "TEXT")
    private String selfComments;

    @Column(name = "manager_comments", columnDefinition = "TEXT")
    private String managerComments;

    @Column(name = "hr_comments", columnDefinition = "TEXT")
    private String hrComments;

    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths;

    @Column(name = "areas_of_improvement", columnDefinition = "TEXT")
    private String areasOfImprovement;

    @Column(name = "is_high_potential")
    @Builder.Default
    private boolean highPotential = false;

    @Column(name = "succession_readiness")
    private String successionReadiness;

    @Column(name = "submitted_at")
    private LocalDate submittedAt;

    @Column(name = "calibrated_at")
    private LocalDate calibratedAt;

    @Column(name = "published_at")
    private LocalDate publishedAt;

    public enum ReviewType { SELF, MANAGER, PEER, SUBORDINATE, ANNUAL, MID_YEAR }
    public enum ReviewStage { DRAFT, SUBMITTED, MANAGER_REVIEW, CALIBRATION, PUBLISHED }
}
