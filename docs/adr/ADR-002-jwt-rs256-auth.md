# ADR-002: JWT RS256 with Redis Token Blacklist

**Status:** Accepted  
**Date:** 2026-04-15  
**Context:** Need stateless auth that supports instant revocation for logout/account lock.  
**Decision:** JWT signed with RS256 (asymmetric keys). Access tokens (15 min TTL) + refresh tokens (7 day TTL). On logout, token JTI added to Redis blacklist with TTL matching token expiry.  
**Alternatives Considered:** Opaque tokens (rejected — requires DB lookup per request), symmetric HS256 (rejected — can't share public key with gateway).  
**Consequences:** Stateless verification at gateway. Blacklist check adds ~1ms latency. Redis required for auth service.
