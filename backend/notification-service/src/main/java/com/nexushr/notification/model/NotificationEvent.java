package com.nexushr.notification.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationEvent {

    private String id;
    private String tenantId;
    private String recipientId;
    private String recipientEmail;
    private String recipientPhone;

    private NotificationType type;
    private NotificationChannel channel;

    private String subject;
    private String templateId;
    private Map<String, String> templateData;

    private String body;
    private Priority priority;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    public enum NotificationType {
        LEAVE_APPLIED, LEAVE_APPROVED, LEAVE_REJECTED,
        PAYROLL_PROCESSED, PAYSLIP_GENERATED,
        REVIEW_ASSIGNED, REVIEW_SUBMITTED, REVIEW_PUBLISHED,
        ONBOARDING_WELCOME, PROBATION_ENDING, CONTRACT_EXPIRY,
        ATTENDANCE_REMINDER, SYSTEM_ALERT
    }

    public enum NotificationChannel {
        EMAIL, SMS, PUSH, IN_APP, WEBHOOK
    }

    public enum Priority {
        LOW, NORMAL, HIGH, URGENT
    }

    public enum NotificationStatus {
        PENDING, SENT, DELIVERED, FAILED, BOUNCED
    }
}
