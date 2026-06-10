package com.smagyo.order;

import com.smagyo.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    // ── Admin ────────────────────────────────────────────────────────────────

    public Page<OrderDto> listForAdmin(Pageable pageable) {
        return orderRepository.findByTenantId(requireTenantId(), pageable).map(OrderDto::from);
    }

    @Transactional
    public OrderDto updateStatus(String orderId, String status) {
        Order order = orderRepository.findByIdAndTenantId(orderId, requireTenantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        return OrderDto.from(orderRepository.save(order));
    }

    // ── Public storefront ────────────────────────────────────────────────────

    @Transactional
    public OrderDto placeOrder(PlaceOrderRequest req) {
        BigDecimal total = req.items().stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .tenantId(req.tenantId())
                .customerName(req.customerName())
                .customerEmail(req.customerEmail())
                .deliveryCity(req.deliveryCity())
                .deliveryAddress(req.deliveryAddress())
                .total(total)
                .build();

        List<OrderItem> items = req.items().stream()
                .map(i -> OrderItem.builder()
                        .order(order)
                        .productId(i.productId())
                        .productName(i.productName())
                        .quantity(i.quantity())
                        .unitPrice(i.unitPrice())
                        .build())
                .toList();

        order.getItems().addAll(items);
        return OrderDto.from(orderRepository.save(order));
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private String requireTenantId() {
        String id = TenantContext.getTenantId();
        if (id == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tenant context missing");
        return id;
    }
}
