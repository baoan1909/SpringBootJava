package com.example.product.infrastructure.persistence.mapper;

import com.example.product.domain.model.Product;
import com.example.product.domain.model.ProductVariant;
import com.example.product.infrastructure.persistence.entity.ProductJpaEntity;
import com.example.product.infrastructure.persistence.entity.ProductVariantJpaEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface ProductEntityMapper {
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    ProductJpaEntity toJpaEntity(Product product);
    Product toDomain(ProductJpaEntity entity);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductVariantJpaEntity toJpaVariantEntity(ProductVariant variant);
    ProductVariant toVariantDomain(ProductVariantJpaEntity entity);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    void updateJpaEntityDomain(Product product, @MappingTarget ProductJpaEntity entity);

    @AfterMapping
    default void linkVariants(@MappingTarget ProductJpaEntity productJpa){
        if(productJpa.getVariants() != null){
            for (ProductVariantJpaEntity variantJpaEntity : productJpa.getVariants()) {
                variantJpaEntity.setProduct(productJpa);

                if (variantJpaEntity.getAttributes() != null) {
                    variantJpaEntity.getAttributes().forEach(attr -> {
                        attr.setVariant(variantJpaEntity);
                    });
                }
            }
        }
    }
}
