package com.nexushr.ai.model;

import com.nexushr.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Stores AI-generated attrition risk predictions per employee.
 * Each prediction is versioned by model tag and includes SHAP-based
 * explainability factors stored as JSONB.
 */
@Entity
@Table(name = "ai_predictions", indexes = {
    @Index(name = "idx_pred_employee", columnList = "employee_id"),
    @Index(name = "idx_pred_score", columnList = "attrition_score")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AttritionPrediction extends AuditableEntity {

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "department")
    private String department;

    @Column(name = "designation")
    private String designation;

    /**
     * Attrition risk score (0.0 - 1.0).
     * >= 0.7 = High Risk, 0.4-0.7 = Medium Risk, < 0.4 = Low Risk
     */
    @Column(name = "attrition_score", precision = 5, scale = 4, nullable = false)
    private BigDecimal attritionScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20)
    private RiskLevel riskLevel;

    /**
     * SHAP-based explainability: JSON object with feature importances.
     * Example: {"tenure": -0.15, "salary_change": 0.25, "performance_trend": -0.10}
     */
    @Column(name = "shap_values", columnDefinition = "TEXT")
    private String shapValues;

    /**
     * Top contributing factors in plain English.
     */
    @Column(name = "top_factors", columnDefinition = "TEXT")
    private String topFactors;

    /**
     * AI-generated retention recommendation.
     */
    @Column(name = "recommendation", columnDefinition = "TEXT")
    private String recommendation;

    @Column(name = "model_version", length = 50)
    private String modelVersion;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    /**
     * Feature values used for this prediction.
     */
    @Column(name = "tenure_months")
    private Integer tenureMonths;

    @Column(name = "performance_rating", precision = 3, scale = 1)
    private BigDecimal performanceRating;

    @Column(name = "salary_change_pct", precision = 5, scale = 2)
    private BigDecimal salaryChangePct;

    @Column(name = "absence_days")
    private Integer absenceDays;

    @Column(name = "months_since_promotion")
    private Integer monthsSincePromotion;

    @Column(name = "engagement_score", precision = 3, scale = 1)
    private BigDecimal engagementScore;

    public enum RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
