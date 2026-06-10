package com.smagyo.tenant;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

/**
 * Represents one flower company (a SAAS tenant).
 * Every tenant gets their own subdomain: {subdomain}.smagyo.com
 * All products, orders, and users belonging to this company reference this tenant's id.
 */
@Entity
@Table(name = "tenants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Tenant {

    @Id
    @UuidGenerator
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @Column(nullable = false)
    private String name;                         // "Rose Garden Shop"

    @Column(unique = true, nullable = false)
    private String subdomain;                    // "rosegarden" → rosegarden.smagyo.com

    @Column(name = "custom_domain")
    private String customDomain;                 // "myshop.com" (Pro/Enterprise only)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TenantPlan plan = TenantPlan.STARTER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TenantStatus status = TenantStatus.TRIAL;

    // Branding (synced from AdminSettings)
    @Column(name = "primary_color") @Builder.Default private String primaryColor = "#2D6A4F";
    @Column(name = "accent_color")  @Builder.Default private String accentColor  = "#84A98C";
    @Column(name = "logo_emoji")    @Builder.Default private String logoEmoji    = "🌹";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum TenantPlan   { STARTER, PRO, ENTERPRISE }
    public enum TenantStatus { TRIAL, ACTIVE, SUSPENDED }
}
