package com.smagyo.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDto(
        String id,
        String tenantId,
        String customerName,
        String customerEmail,
        String deliveryCity,
        String deliveryAddress,
        BigDecimal total,
        String status,
        List<OrderItemDto> items,
        Instant createdAt
) {
    public record OrderItemDto(String productId, String productName, Integer quantity, BigDecimal unitPrice) {}

    public static OrderDto from(Order o) {
        List<OrderItemDto> items = o.getItems().stream()
                .map(i -> new OrderItemDto(i.getProductId(), i.getProductName(), i.getQuantity(), i.getUnitPrice()))
                .toList();
        return new OrderDto(o.getId(), o.getTenantId(), o.getCustomerName(), o.getCustomerEmail(),
                o.getDeliveryCity(), o.getDeliveryAddress(), o.getTotal(),
                o.getStatus().name(), items, o.getCreatedAt());
    }
}
