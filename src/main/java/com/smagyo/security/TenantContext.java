package com.smagyo.security;

/**
 * Holds the current tenant ID for the duration of one HTTP request thread.
 * Usage in services:
 *   String tenantId = TenantContext.getTenantId();
 */
public final class TenantContext {

    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setTenantId(String tenantId) {
        CURRENT.set(tenantId);
    }

    public static String getTenantId() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
