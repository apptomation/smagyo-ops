package com.smagyo.product;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductDto(
        String     id,
        String     tenantId,
        String     name,
        String     description,
        BigDecimal price,
        BigDecimal originalPrice,
        Integer    stock,
        Boolean    active,
        String     category,
        String     occasion,
        String     emoji,
        String     imageUrl,
        Instant    createdAt
) {
    public static ProductDto from(Product p) {
        return new ProductDto(
                p.getId(), p.getTenantId(), p.getName(), p.getDescription(),
                p.getPrice(), p.getOriginalPrice(), p.getStock(), p.getActive(),
                p.getCategory(), p.getOccasion(), p.getEmoji(), p.getImageUrl(),
                p.getCreatedAt()
        );
    }
}
