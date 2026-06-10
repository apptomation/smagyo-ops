package com.smagyo.auth;

/**
 * Returned after a successful login.
 * The React frontend stores this in state (AdminAuthContext) and sends
 * the token as: Authorization: Bearer {token}
 */
public record LoginResponse(
        String token,
        String role,         // "SUPER_ADMIN" | "TENANT_ADMIN"
        String tenantId,     // null for SUPER_ADMIN
        String tenantName,
        String name,
        String avatar        // initials, e.g. "AJ"
) {}
