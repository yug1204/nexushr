package com.nexushr.attendance.model;

import com.nexushr.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "leave_requests", indexes = {
    @Index(name = "idx_leave_emp", columnList = "employeeId"),
    @Index(name = "idx_leave_status", columnList = "status"),
    @Index(name = "idx_leave_tenant", columnList = "tenantId, status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaveRequest extends AuditableEntity {

    @Column(nullable = false)
    private String employeeId;

    private String employeeName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private int totalDays;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LeaveStatus status = LeaveStatus.PENDING;

    private String approverId;
    private String approverName;
    private LocalDate approvedAt;
    private String approverRemarks;

    // Half-day flags
    @Builder.Default
    private boolean halfDayStart = false;
    @Builder.Default
    private boolean halfDayEnd = false;

    // Attachment (medical certificate etc.)
    private String attachmentUrl;

    public enum LeaveType {
        CASUAL_LEAVE, SICK_LEAVE, EARNED_LEAVE, MATERNITY_LEAVE,
        PATERNITY_LEAVE, COMPENSATORY_OFF, LOSS_OF_PAY, BEREAVEMENT
    }

    public enum LeaveStatus {
        PENDING, APPROVED, REJECTED, CANCELLED, WITHDRAWN
    }
}
