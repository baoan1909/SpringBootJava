package com.example.product.domain.model;

import com.example.product.domain.common.Default;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProductVariant extends Auditable {
    private Long id;
    private BigDecimal price;
    private Integer stockQuantity;
    private String skuCode;
    private final List<ProductVariantAttribute> attributes = new ArrayList<>();

    public ProductVariant(BigDecimal price, Integer stockQuantity, String skuCode, List<ProductVariantAttribute> attributes) {

        if (price == null || price.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Giá bán không được để trống và không được âm");
        }
        if (stockQuantity == null || stockQuantity < 0){
            throw new IllegalArgumentException("Số lượng tồn kho không được để trống và không được âm");
        }

        this.price = price;
        this.stockQuantity = stockQuantity;
        this.skuCode = skuCode;
        if(attributes != null){
            this.attributes.addAll(attributes);
        }
    }

    @Default
    public ProductVariant(Long id, BigDecimal price, Integer stockQuantity, String skuCode, List<ProductVariantAttribute> attributes) {
        this.id = id;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.skuCode = skuCode;
        if(attributes != null){
            this.attributes.addAll(attributes);
        }
    }

    public void updatePriceAndStock(BigDecimal price, Integer stockQuantity){
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Giá bán không được để trống và không được âm");
        }
        if (stockQuantity == null || stockQuantity < 0){
            throw new IllegalArgumentException("Số lượng tồn kho không được để trống và không được âm");
        }
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public String getDetailedVariantSummary() {
        if (this.attributes == null || this.attributes.isEmpty()) {
            return "Mặc định";
        }

        return this.attributes.stream()
                .filter(attr -> attr.getValue() != null && !attr.getValue().isBlank())
                .map(attr -> attr.getName() + ": " + attr.getValue())
                .collect(Collectors.joining(", "));
    }
}
