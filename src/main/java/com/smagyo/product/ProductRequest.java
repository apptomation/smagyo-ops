package com.smagyo.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank
        String name,

        String description,

        @NotNull @DecimalMin("0.01")
        BigDecimal price,

        BigDecimal originalPrice,

        @NotNull @Min(0)
        Integer stock,

        Boolean active,
        String category,
        String occasion,
        String emoji,
        String imageUrl
) {}
