package com.nexushr.ai.model;

import com.nexushr.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Represents a skill gap analysis result for an employee.
 * Compares current skills (from employee profile) against
 * required skills (from job description) using NLP similarity.
 */
@Entity
@Table(name = "skill_gaps")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SkillGap extends AuditableEntity {

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "department")
    private String department;

    @Column(name = "skill_name", nullable = false, length = 100)
    private String skillName;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_category", length = 50)
    private SkillCategory category;

    /** Current proficiency level (0-100) */
    @Column(name = "current_level", precision = 5, scale = 2)
    private BigDecimal currentLevel;

    /** Required proficiency level for the role (0-100) */
    @Column(name = "required_level", precision = 5, scale = 2)
    private BigDecimal requiredLevel;

    /** Gap = required - current */
    @Column(name = "gap_score", precision = 5, scale = 2)
    private BigDecimal gapScore;

    /** AI-generated learning recommendation */
    @Column(name = "learning_recommendation", columnDefinition = "TEXT")
    private String learningRecommendation;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private Priority priority;

    public enum SkillCategory {
        TECHNICAL, LEADERSHIP, COMMUNICATION, DOMAIN, ANALYTICAL
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
