package com.example.product.domain.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(Long id) {
        super("Product Không được tìm thấy với id: " + id);
    }
}
