package com.example.product.application.mapper;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.application.dto.response.ProductVariantResponse;
import com.example.product.domain.model.Product;
import com.example.product.domain.model.ProductVariant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {
    ProductResponse toResponse(Product product);
    ProductVariantResponse toVariantResponse(ProductVariant variant);
}
