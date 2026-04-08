package com.example.product.application.dto.command;

import java.math.BigDecimal;

public record UpdateProductCommand(
        String name,
        BigDecimal price
) {
}
