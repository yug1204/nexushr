package com.nexushr.attendance.model;

import com.nexushr.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;

@Entity
@Table(name = "attendance_records", indexes = {
    @Index(name = "idx_att_emp_date", columnList = "employeeId, attendanceDate"),
    @Index(name = "idx_att_tenant_date", columnList = "tenantId, attendanceDate")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AttendanceRecord extends AuditableEntity {

    @Column(nullable = false)
    private String employeeId;

    @Column(nullable = false)
    private LocalDate attendanceDate;

    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AttendanceStatus status = AttendanceStatus.ABSENT;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ClockSource clockSource = ClockSource.WEB;

    private String clockInLocation;  // GPS or office name
    private String clockOutLocation;

    private Double totalHoursWorked;
    private Double overtimeHours;
    private String remarks;

    // IP/device tracking for audit
    private String clockInIp;
    private String clockOutIp;

    public enum AttendanceStatus {
        PRESENT, ABSENT, HALF_DAY, ON_LEAVE, HOLIDAY, WEEK_OFF, WORK_FROM_HOME
    }

    public enum ClockSource {
        WEB, MOBILE, BIOMETRIC, RFID, MANUAL
    }

    @PreUpdate
    public void calculateHours() {
        if (clockInTime != null && clockOutTime != null) {
            Duration duration = Duration.between(clockInTime, clockOutTime);
            this.totalHoursWorked = duration.toMinutes() / 60.0;
            this.overtimeHours = Math.max(0, this.totalHoursWorked - 8.0);
        }
    }
}
