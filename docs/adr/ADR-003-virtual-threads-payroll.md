# ADR-003: Virtual Threads for Payroll Bulk Processing

**Status:** Accepted  
**Date:** 2026-04-20  
**Context:** Payroll runs process 5,000-50,000 employees. Each calculation is CPU-light but involves DB reads. Platform threads limit concurrency.  
**Decision:** Use Java 21 virtual threads (Project Loom) via `parallelStream()` on virtual thread executor. Each employee payslip calculated independently.  
**Alternatives Considered:** Reactive (WebFlux) — rejected due to complexity and team skill gap. Thread pool — rejected due to throughput ceiling.  
**Consequences:** 10x throughput vs platform threads. No reactive complexity. Requires Java 21 runtime. Compatible with Spring Boot 3.3 virtual thread support.
