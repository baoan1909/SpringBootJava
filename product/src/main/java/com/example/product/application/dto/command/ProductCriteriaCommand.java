package com.example.product.application.dto.command;

import java.math.BigDecimal;

public record ProductCriteriaCommand(
        String keyWord,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String status,
        String ownerEmail,
        Integer page,
        Integer size
) {
    public ProductCriteriaCommand{
        if (page == null || page < 0){
            page = 0;

        }
        if(size == null || size <= 0){
            size = 10;
        }

        if (size > 100){
            size = 100;
        }

        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0){
            throw new IllegalArgumentException("minPrice không được lớn hơn maxPrice");
        }

        if (keyWord != null){
            keyWord = keyWord.trim();
        }

        if(status != null){
            status = status.trim();
            if (status.equalsIgnoreCase("ALL")){
                status = null;
            }
        }
    }

    public ProductCriteriaCommand withStatus(String status) {
        return new ProductCriteriaCommand(
                this.keyWord, this.minPrice, this.maxPrice,
                status, this.ownerEmail, this.page, this.size
        );
    }

    public ProductCriteriaCommand withOwnerEmail(String ownerEmail) {
        return new ProductCriteriaCommand(
                this.keyWord, this.minPrice, this.maxPrice,
                this.status, ownerEmail, this.page, this.size
        );
    }
}
