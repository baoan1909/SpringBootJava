package com.example.product.application.dto;

import java.math.BigDecimal;

public record UpdateProductCommand(
        String name,
        BigDecimal price
) {
}
