package com.example.product.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String slug,
        String description,
        String status,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer totalStock,
        List<ProductVariantResponse> variants
) {
    public record ProductVariantResponse(
            Long id,
            String skuCode,
            BigDecimal price,
            Integer stockQuantity,
            String variantSummary,
            List<AttributeResponse> attributes
    ){}

    public record AttributeResponse(
            String name,
            String value
    ){}
}
