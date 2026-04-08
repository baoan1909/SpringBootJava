package com.example.product.application.dto.command;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductCommand(
        String sku,
        String name,
        BigDecimal price,
        List<VariantItem> variants
) {
    public record VariantItem(
            String color,
            String size,
            BigDecimal additionalPrice
    ){

    }

    public CreateProductCommand{
        if (variants == null){
            variants = List.of();
        }
    }
}
