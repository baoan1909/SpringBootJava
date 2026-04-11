package com.example.product.application.dto.command;

import java.math.BigDecimal;
import java.util.List;

public record CreateVariantsCommand(
        List<String> colors,
        List<String> sizes,
        BigDecimal additionalPrice
) {
}
