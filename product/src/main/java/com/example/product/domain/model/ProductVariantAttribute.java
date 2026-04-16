package com.example.product.domain.model;

import lombok.Getter;

@Getter
public class ProductVariantAttribute extends Auditable {
    private String name;
    private String value;

    public ProductVariantAttribute(String name, String value) {
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("name khồn được để trống");
        }
        if(value == null || value.isBlank()){
            throw new IllegalArgumentException("Cần ít nhất 1 biến thể");
        }

        this.name = name;
        this.value = value;
    }
}
