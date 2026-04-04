package com.example.product.domain.model;

import jakarta.persistence.Entity;

import java.math.BigDecimal;

public class Product {
    private final Long id;
    private final String sku;
    private String name;
    private BigDecimal price;

    public Product(Long id, String sku, String name, BigDecimal price) {
        if (sku == null || sku.isBlank()){
            throw new IllegalArgumentException("SKU không được để trống");
        }

        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Name không được để trống");
        }

        if ( price == null || price.compareTo(BigDecimal.ZERO) <= 0 ){
            throw new IllegalArgumentException("Price phải lớn hơn 0 đồng");
        }

        this.id = id;
        this.sku = sku;
        this.name = name;
        this.price = price;
    }

    public void updateInfo(String name, BigDecimal price) {
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Name không được để trống");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0 ){
            throw new IllegalArgumentException("Price phải lớn hơn 0 đồng");
        }

        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
