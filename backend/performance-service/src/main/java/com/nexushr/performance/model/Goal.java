package com.nexushr.performance.model;

import com.nexushr.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "goals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Goal extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "parent_goal_id")
    private String parentGoalId;

    @Column(name = "review_cycle_id")
    private String reviewCycleId;

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal weight = new BigDecimal("1.0");

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal progress = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GoalStatus status = GoalStatus.NOT_STARTED;

    @Column(name = "key_result", columnDefinition = "TEXT")
    private String keyResult;

    @Column(name = "target_value")
    private String targetValue;

    @Column(name = "current_value")
    private String currentValue;

    public enum GoalStatus { NOT_STARTED, IN_PROGRESS, COMPLETED, DEFERRED, CANCELLED }
}
