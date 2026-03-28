package com.example.product.infrastructure.persistence.mapper;

import com.example.product.domain.model.Product;
import com.example.product.infrastructure.persistence.entity.ProductJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductJpaEntity toJpaEntity(Product product) {
        return new ProductJpaEntity(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getPrice()
        );
    }

    public Product toDomain(ProductJpaEntity entity) {
        return new Product(
                entity.getId(),
                entity.getSku(),
                entity.getName(),
                entity.getPrice()
        );
    }
}
