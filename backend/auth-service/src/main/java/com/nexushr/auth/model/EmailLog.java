package com.nexushr.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity to log all outbound emails for audit trail and debugging.
 * Adapted from batch 8 TaskManagementTool_B8 EmailLog entity.
 */
@Entity
@Table(name = "email_log")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Column(nullable = false)
    private String subject;

    @Column(length = 5000)
    private String body;

    @Column(name = "sent_status")
    private boolean sentStatus;

    @Column(name = "sent_at")
    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();

    public EmailLog(String recipientEmail, String subject, String body, boolean sentStatus) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.body = body;
        this.sentStatus = sentStatus;
        this.sentAt = LocalDateTime.now();
    }
}
