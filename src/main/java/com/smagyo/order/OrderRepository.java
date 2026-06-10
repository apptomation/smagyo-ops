package com.smagyo.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {

    Page<Order> findByTenantId(String tenantId, Pageable pageable);

    /** Used for ownership check before status update */
    Optional<Order> findByIdAndTenantId(String id, String tenantId);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o " +
           "WHERE o.tenantId = :tenantId AND o.status != 'CANCELLED'")
    BigDecimal sumRevenueByTenantId(String tenantId);

    long countByTenantId(String tenantId);
}
