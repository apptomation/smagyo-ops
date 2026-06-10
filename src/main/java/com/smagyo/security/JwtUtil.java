package com.smagyo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a JWT containing role, tenantId and tenantName as custom claims.
     * The React frontend reads these to drive role-based UI without extra API calls.
     */
    public String generateToken(String email, String role, String tenantId, String tenantName, String userId) {
        return Jwts.builder()
                .subject(email)
                .claim("userId",     userId)
                .claim("role",       role)
                .claim("tenantId",   tenantId)
                .claim("tenantName", tenantName)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token)    { return extractAllClaims(token).getSubject(); }
    public String extractRole(String token)     { return extractAllClaims(token).get("role",     String.class); }
    public String extractTenantId(String token) { return extractAllClaims(token).get("tenantId", String.class); }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
