package com.example.product.application.dto.response;

import java.math.BigDecimal;

public record ProductVariantResponse(
        Long id,
        String color,
        String size,
        BigDecimal additionalPrice
) {
}
