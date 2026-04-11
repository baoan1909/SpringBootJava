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
            List<String> colors,
            List<String> sizes,
            BigDecimal additionalPrice
    ){

    }

    public CreateProductCommand(String sku, String name, BigDecimal price, List<VariantItem> variants) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.variants = (variants == null) ? List.of() : variants;
    }
}
