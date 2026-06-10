package com.smagyo.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ── Public storefront ────────────────────────────────────────────────────

    /** GET /api/products?tenantId=xxx — active products for one storefront */
    @GetMapping
    public List<ProductDto> listPublic(@RequestParam String tenantId) {
        return productService.listPublic(tenantId);
    }

    /** GET /api/products/{id}?tenantId=xxx */
    @GetMapping("/{id}")
    public ProductDto getPublic(@PathVariable String id, @RequestParam String tenantId) {
        return productService.getPublic(tenantId, id);
    }

    // ── Admin (JWT required, tenantId from TenantContext) ────────────────────

    /** GET /api/products/admin?page=0&size=20 */
    @GetMapping("/admin")
    public Page<ProductDto> listForAdmin(Pageable pageable) {
        return productService.listForAdmin(pageable);
    }

    /** POST /api/products/admin */
    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    /** PUT /api/products/admin/{id} */
    @PutMapping("/admin/{id}")
    public ProductDto update(@PathVariable String id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    /** DELETE /api/products/admin/{id} */
    @DeleteMapping("/admin/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        productService.delete(id);
    }
}
