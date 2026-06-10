package com.smagyo.product;

import com.smagyo.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // ── Admin (authenticated, tenantId from TenantContext) ──────────────────

    public Page<ProductDto> listForAdmin(Pageable pageable) {
        return productRepository
                .findByTenantId(requireTenantId(), pageable)
                .map(ProductDto::from);
    }

    @Transactional
    public ProductDto create(ProductRequest req) {
        String tenantId = requireTenantId();
        Product product = Product.builder()
                .tenantId(tenantId)
                .name(req.name())
                .description(req.description())
                .price(req.price())
                .originalPrice(req.originalPrice())
                .stock(req.stock())
                .active(req.active() != null ? req.active() : true)
                .category(req.category())
                .occasion(req.occasion())
                .emoji(req.emoji())
                .imageUrl(req.imageUrl())
                .build();
        return ProductDto.from(productRepository.save(product));
    }

    @Transactional
    public ProductDto update(String id, ProductRequest req) {
        Product product = findOwnedOrThrow(id);
        product.setName(req.name());
        product.setDescription(req.description());
        product.setPrice(req.price());
        product.setOriginalPrice(req.originalPrice());
        product.setStock(req.stock());
        if (req.active()    != null) product.setActive(req.active());
        if (req.category()  != null) product.setCategory(req.category());
        if (req.occasion()  != null) product.setOccasion(req.occasion());
        if (req.emoji()     != null) product.setEmoji(req.emoji());
        if (req.imageUrl()  != null) product.setImageUrl(req.imageUrl());
        return ProductDto.from(productRepository.save(product));
    }

    @Transactional
    public void delete(String id) {
        Product product = findOwnedOrThrow(id);
        productRepository.delete(product);
    }

    // ── Public storefront (tenantId from query param) ────────────────────────

    public List<ProductDto> listPublic(String tenantId) {
        return productRepository.findByTenantIdAndActiveTrue(tenantId)
                .stream().map(ProductDto::from).toList();
    }

    public ProductDto getPublic(String tenantId, String productId) {
        return productRepository.findByIdAndTenantId(productId, tenantId)
                .filter(Product::getActive)
                .map(ProductDto::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String requireTenantId() {
        String id = TenantContext.getTenantId();
        if (id == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tenant context missing");
        return id;
    }

    private Product findOwnedOrThrow(String productId) {
        return productRepository.findByIdAndTenantId(productId, requireTenantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }
}
