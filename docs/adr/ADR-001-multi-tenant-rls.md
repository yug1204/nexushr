# ADR-001: Multi-Tenant Row-Level Security

**Status:** Accepted  
**Date:** 2026-04-15  
**Context:** NexusHR serves multiple enterprise clients on shared infrastructure.  
**Decision:** Use `tenant_id` column on all tables with Hibernate filters injecting tenant context at query layer. Row-level isolation without schema-per-tenant overhead.  
**Consequences:** Simpler ops (single schema), requires discipline to always include tenant filter. Tested via integration tests.
