package com.smagyo.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Self-registration for a new flower company (creates Tenant + TENANT_ADMIN user).
 */
public record RegisterRequest(
        @NotBlank @Size(min = 2, max = 100)
        String name,           // owner full name

        @NotBlank @Email
        String email,

        @NotBlank @Size(min = 6, max = 100)
        String password,

        @NotBlank @Size(min = 2, max = 100)
        String storeName,      // "Rose Garden Shop"

        @NotBlank @Pattern(regexp = "^[a-z0-9-]{2,30}$",
                message = "Subdomain must be 2-30 lowercase letters, digits, or hyphens")
        String subdomain       // "rosegarden" → rosegarden.smagyo.com
) {}
