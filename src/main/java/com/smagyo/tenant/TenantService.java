package com.smagyo.tenant;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    public Page<TenantDto> listAll(Pageable pageable) {
        return tenantRepository.findAll(pageable).map(TenantDto::from);
    }

    public TenantDto getById(String id) {
        return TenantDto.from(findOrThrow(id));
    }

    @Transactional
    public TenantDto updateStatus(String id, String status) {
        Tenant tenant = findOrThrow(id);
        tenant.setStatus(Tenant.TenantStatus.valueOf(status.toUpperCase()));
        return TenantDto.from(tenantRepository.save(tenant));
    }

    @Transactional
    public TenantDto updateBranding(String id, TenantBrandingRequest req) {
        Tenant tenant = findOrThrow(id);
        if (req.primaryColor() != null) tenant.setPrimaryColor(req.primaryColor());
        if (req.accentColor()  != null) tenant.setAccentColor(req.accentColor());
        if (req.logoEmoji()    != null) tenant.setLogoEmoji(req.logoEmoji());
        if (req.customDomain() != null) tenant.setCustomDomain(req.customDomain());
        return TenantDto.from(tenantRepository.save(tenant));
    }

    private Tenant findOrThrow(String id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
    }

    /** Branding update DTO — used by both tenant admin (self) and super admin */
    public record TenantBrandingRequest(
            String primaryColor,
            String accentColor,
            String logoEmoji,
            String customDomain
    ) {}
}
