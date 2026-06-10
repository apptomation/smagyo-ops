package com.smagyo.tenant;

import java.time.Instant;

public record TenantDto(
        String  id,
        String name,
        String subdomain,
        String customDomain,
        String plan,
        String status,
        String primaryColor,
        String accentColor,
        String logoEmoji,
        Instant createdAt
) {
    public static TenantDto from(Tenant t) {
        return new TenantDto(
                t.getId(), t.getName(), t.getSubdomain(), t.getCustomDomain(),
                t.getPlan().name(), t.getStatus().name(),
                t.getPrimaryColor(), t.getAccentColor(), t.getLogoEmoji(),
                t.getCreatedAt()
        );
    }
}
