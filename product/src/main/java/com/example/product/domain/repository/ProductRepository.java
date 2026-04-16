package com.example.product.domain.repository;

import com.example.product.application.common.Pagination;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Pagination<Product> findAll(ProductCriteriaCommand criteriaCommand);
    Optional<Product> findById(Long id);
    void deleteById(Long id);
    boolean existsById(Long id);
}
