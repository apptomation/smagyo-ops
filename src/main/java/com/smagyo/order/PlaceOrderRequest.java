package com.smagyo.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

/** Posted from the public storefront checkout */
public record PlaceOrderRequest(
        @NotNull
        String tenantId,

        @NotBlank
        String customerName,

        @NotBlank @Email
        String customerEmail,

        String deliveryCity,
        String deliveryAddress,

        @NotNull
        List<OrderLineRequest> items
) {
    public record OrderLineRequest(
            String productId,
            @NotBlank String productName,
            @NotNull Integer quantity,
            @NotNull @DecimalMin("0.01") BigDecimal unitPrice
    ) {}
}
