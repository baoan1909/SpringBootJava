package com.example.product.application.dto;

import java.math.BigDecimal;

public record CreateProductCommand(
        String sku,
        String name,
        BigDecimal price
) {
}
