package com.example.product.domain.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductVariant {
    private Long id;
    private String color;
    private String size;
    private BigDecimal additionalPrice;

    public ProductVariant(Long id, String color, String size, BigDecimal additionalPrice) {
        if ((color == null || color.isBlank()) && (size == null || size.isBlank())) {
            throw new IllegalArgumentException("Bạn cần chọn 1 biến thể");
        }
        if (additionalPrice == null || additionalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá phụ thu không được âm");
        }

        this.id = id;
        this.color = color;
        this.size = size;
        this.additionalPrice = additionalPrice;
    }

    public void updateAdditionalPrice(BigDecimal additionalPrice) {
        if (additionalPrice == null || additionalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá phụ thu không được âm");
        }

        this.additionalPrice = additionalPrice;
    }
}
