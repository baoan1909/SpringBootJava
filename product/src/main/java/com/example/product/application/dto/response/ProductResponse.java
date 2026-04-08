package com.example.product.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        BigDecimal price,
        List<ProductVariantResponse> variants
) {
}
