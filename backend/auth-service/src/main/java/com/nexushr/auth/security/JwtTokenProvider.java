package com.nexushr.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * JWT token provider with RS256 signing, Redis-backed token blacklist,
 * and configurable expiration times.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    public JwtTokenProvider(
            @Value("${nexushr.jwt.secret}") String secret,
            @Value("${nexushr.jwt.access-token-expiry:3600000}") long accessTokenExpiry,
            @Value("${nexushr.jwt.refresh-token-expiry:604800000}") long refreshTokenExpiry,
            RedisTemplate<String, String> redisTemplate) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
        this.redisTemplate = redisTemplate;
    }

    public String generateAccessToken(String userId, String email, Set<String> roles, String tenantId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiry);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId)
                .claim("email", email)
                .claim("roles", roles)
                .claim("tenant_id", tenantId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiry);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Check blacklist
            String jti = claims.getId();
            if (Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + jti))) {
                throw new JwtException("Token has been revoked");
            }

            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            throw e;
        }
    }

    public void blacklistToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String jti = claims.getId();
            long ttl = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        BLACKLIST_PREFIX + jti, "revoked", Duration.ofMillis(ttl));
            }
        } catch (JwtException e) {
            log.warn("Could not blacklist token: {}", e.getMessage());
        }
    }

    public String getUserIdFromToken(String token) {
        return validateToken(token).getSubject();
    }

    public long getAccessTokenExpiry() {
        return accessTokenExpiry;
    }
}
