package com.example.product.application.dto.command;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductCommand(
        String name,
        String slug,
        String description,
        List<VariantItem> variants
) {
    public CreateProductCommand {
        variants = (variants == null) ? List.of() : variants;
    }
    public record VariantItem(
            BigDecimal price,
            Integer stockQuantity,
            String skuCode,
            List<AttributeItem> attributes
    ){
        public VariantItem {
            attributes = ((attributes == null) ? List.of() : attributes);
        }

        public record AttributeItem(
                String name,
                String value
        ){}
    }
}
