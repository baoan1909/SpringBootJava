package com.example.product.domain.repository;

import com.example.product.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Optional<Product> findBySku(String sku);
    void deleteById(Long id);
    boolean existsById(Long id);
}
