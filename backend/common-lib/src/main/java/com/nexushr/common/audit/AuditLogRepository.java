package com.nexushr.common.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntry, Long> {

    Optional<AuditLogEntry> findTopByTenantIdOrderByIdDesc(String tenantId);

    List<AuditLogEntry> findByTenantIdOrderByIdAsc(String tenantId);

    List<AuditLogEntry> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, String entityId);

    List<AuditLogEntry> findByActorIdOrderByTimestampDesc(String actorId);
}
