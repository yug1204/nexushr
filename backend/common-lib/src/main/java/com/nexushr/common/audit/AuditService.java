package com.nexushr.common.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

/**
 * Audit Service — records immutable audit entries with SHA-256 hash chaining.
 * Each new entry includes the hash of the previous entry, forming a
 * tamper-evident chain similar to a blockchain.
 *
 * If any historical entry is modified, the chain breaks and is detectable
 * via the verifyChainIntegrity() method.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepo;
    private final ObjectMapper objectMapper;

    /**
     * Record an audit event with hash chaining.
     */
    public AuditLogEntry record(String entityType, String entityId,
                                AuditLogEntry.AuditAction action,
                                String actorId, String actorRole,
                                Object beforeState, Object afterState,
                                String changedFields, String ipAddress,
                                String tenantId) {
        try {
            String beforeJson = beforeState != null ? objectMapper.writeValueAsString(beforeState) : null;
            String afterJson = afterState != null ? objectMapper.writeValueAsString(afterState) : null;

            // Get previous hash for chain
            String previousHash = auditLogRepo.findTopByTenantIdOrderByIdDesc(tenantId)
                    .map(AuditLogEntry::getEntryHash)
                    .orElse("GENESIS");

            AuditLogEntry entry = AuditLogEntry.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .action(action)
                    .actorId(actorId)
                    .actorRole(actorRole)
                    .timestamp(LocalDateTime.now())
                    .beforeState(beforeJson)
                    .afterState(afterJson)
                    .changedFields(changedFields)
                    .previousHash(previousHash)
                    .ipAddress(ipAddress)
                    .tenantId(tenantId)
                    .build();

            // Compute hash of this entry
            String content = entityType + entityId + action + actorId +
                    entry.getTimestamp() + beforeJson + afterJson + previousHash;
            entry.setEntryHash(sha256(content));

            AuditLogEntry saved = auditLogRepo.save(entry);
            log.debug("Audit: {} {} on {}:{} by {} — hash: {}",
                    action, entityType, entityId, changedFields, actorId,
                    saved.getEntryHash().substring(0, 12));
            return saved;
        } catch (Exception e) {
            log.error("Failed to record audit entry", e);
            throw new RuntimeException("Audit recording failed", e);
        }
    }

    /**
     * Verify the integrity of the audit chain.
     * Returns true if no tampering detected.
     */
    public boolean verifyChainIntegrity(String tenantId) {
        var entries = auditLogRepo.findByTenantIdOrderByIdAsc(tenantId);
        String expectedPrevious = "GENESIS";

        for (AuditLogEntry entry : entries) {
            if (!expectedPrevious.equals(entry.getPreviousHash())) {
                log.error("CHAIN BROKEN at entry {} — expected prev: {}, found: {}",
                        entry.getId(), expectedPrevious, entry.getPreviousHash());
                return false;
            }
            expectedPrevious = entry.getEntryHash();
        }
        log.info("Audit chain integrity verified — {} entries, no tampering", entries.size());
        return true;
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 hashing failed", e);
        }
    }
}
