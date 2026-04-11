package com.example.product.infrastructure.persistence.mapper;

import com.example.product.domain.model.Product;
import com.example.product.domain.model.ProductVariant;
import com.example.product.infrastructure.persistence.entity.ProductJpaEntity;
import com.example.product.infrastructure.persistence.entity.ProductVariantJpaEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {
    ProductJpaEntity toJpaEntity(Product product);
    Product toDomain(ProductJpaEntity entity);

    ProductVariantJpaEntity toJpaVariantEntity(ProductVariant variant);
    ProductVariant toVariantDomain(ProductVariantJpaEntity entity);

    void updateJpaEntityDomain(Product product, @MappingTarget ProductJpaEntity entity);

    @AfterMapping
    default void linkVariants(@MappingTarget ProductJpaEntity productJpa){
        if(productJpa.getVariants() != null){
            productJpa.getVariants().forEach(variant -> variant.setProduct(productJpa));
        }
    }
}
