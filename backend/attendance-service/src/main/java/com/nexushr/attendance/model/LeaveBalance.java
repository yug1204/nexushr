package com.nexushr.attendance.model;

import com.nexushr.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_balances", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employeeId", "leaveType", "year", "tenantId"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaveBalance extends AuditableEntity {

    @Column(nullable = false)
    private String employeeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveRequest.LeaveType leaveType;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    @Builder.Default
    private double totalEntitled = 0;

    @Builder.Default
    private double used = 0;

    @Builder.Default
    private double pending = 0;  // Pending approval leaves

    @Builder.Default
    private double carriedForward = 0;

    public double getAvailable() {
        return totalEntitled + carriedForward - used - pending;
    }
}
