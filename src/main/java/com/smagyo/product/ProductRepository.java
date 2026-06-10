package com.smagyo.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

    /** Admin: paginated, all statuses */
    Page<Product> findByTenantId(String tenantId, Pageable pageable);

    /** Public storefront: active products only */
    List<Product> findByTenantIdAndActiveTrue(String tenantId);

    /** Used for ownership checks before update/delete */
    Optional<Product> findByIdAndTenantId(String id, String tenantId);
}
