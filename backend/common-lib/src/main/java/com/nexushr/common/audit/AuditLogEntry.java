package com.nexushr.common.audit;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Immutable audit log entry with SHA-256 hash chaining.
 * F-09: Compliance & Audit — SOC 2 CC7.2 compliant.
 * Each entry captures the full before/after state with hash chain
 * for tamper detection.
 */
@Entity
@Table(name = "audit_log", indexes = {
    @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_actor", columnList = "actor_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Column(name = "entity_id", nullable = false, length = 100)
    private String entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private AuditAction action;

    @Column(name = "actor_id", nullable = false, length = 100)
    private String actorId;

    @Column(name = "actor_role", length = 50)
    private String actorRole;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    /** JSON snapshot of entity state BEFORE the change */
    @Column(name = "before_state", columnDefinition = "TEXT")
    private String beforeState;

    /** JSON snapshot of entity state AFTER the change */
    @Column(name = "after_state", columnDefinition = "TEXT")
    private String afterState;

    /** Changed field names (comma-separated) */
    @Column(name = "changed_fields", length = 500)
    private String changedFields;

    /** SHA-256 hash of this entry's content */
    @Column(name = "entry_hash", nullable = false, length = 64)
    private String entryHash;

    /** SHA-256 hash of the previous entry — forms the chain */
    @Column(name = "previous_hash", length = 64)
    private String previousHash;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "tenant_id", nullable = false, length = 100)
    private String tenantId;

    public enum AuditAction {
        CREATE, UPDATE, DELETE, LOGIN, LOGOUT, EXPORT, ACCESS, APPROVE, REJECT
    }
}
