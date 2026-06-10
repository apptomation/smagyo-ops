package com.smagyo.tenant;

import com.smagyo.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /** GET /api/tenants?page=0&size=20 — list all flower companies */
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Page<TenantDto> listAll(Pageable pageable) {
        return tenantService.listAll(pageable);
    }

    /** GET /api/tenants/{id} */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public TenantDto getById(@PathVariable String id) {
        return tenantService.getById(id);
    }

    /** PATCH /api/tenants/{id}/status — body: { "status": "SUSPENDED" } */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public TenantDto updateStatus(@PathVariable String id, @RequestParam String status) {
        return tenantService.updateStatus(id, status);
    }

    /**
     * PATCH /api/tenants/me/branding — tenant admin updates their own store branding.
     * No super_admin check — tenantId comes from JWT via TenantContext.
     */
    @PatchMapping("/me/branding")
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public TenantDto updateMyBranding(@RequestBody TenantService.TenantBrandingRequest req) {
        String tenantId = TenantContext.getTenantId();
        return tenantService.updateBranding(tenantId, req);
    }
}
