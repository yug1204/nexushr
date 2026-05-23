# ADR-005: Event-Driven Architecture with Kafka

**Status:** Accepted  
**Date:** 2026-04-18  
**Context:** HR events (leave approved, payroll completed, review submitted) need to trigger notifications across channels (email, SMS, WebSocket, push) without coupling services.  
**Decision:** Apache Kafka as event bus. Domain services produce events to topic-per-domain (e.g., `hr.leave.approved`, `hr.payroll.completed`). Notification service consumes with at-least-once delivery. Dead-letter queue for failed notifications with 3x retry.  
**Alternatives Considered:** RabbitMQ (rejected — Kafka's durability and replay better for audit requirements), direct REST calls (rejected — tight coupling, no resilience).  
**Consequences:** Loose coupling. Guaranteed delivery. Audit trail via Kafka retention. Adds operational complexity (Zookeeper, broker management). Consumer lag monitoring required.
