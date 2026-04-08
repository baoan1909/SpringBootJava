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
        if (color == null || color.isBlank()){
            throw new IllegalArgumentException("color không được để trống");
        }
        if (size == null || size.isBlank()){
            throw new IllegalArgumentException("size không được để trống");
        }

        this.id = id;
        this.color = color;
        this.size = size;
        this.additionalPrice = additionalPrice != null ? additionalPrice : BigDecimal.ZERO;
    }
}
