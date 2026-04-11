package com.example.product.application.dto.command;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductCommand(
        String name,
        BigDecimal price,
        List<VariantUpdateItem> variants
) {
    public record VariantUpdateItem(
            String color,
            String size,
            BigDecimal additionalPrice
    ){}

    public UpdateProductCommand(String name, BigDecimal price, List<VariantUpdateItem> variants) {
        this.name = name;
        this.price = price;
        this.variants = (variants == null) ? List.of() : variants;
    }
}
