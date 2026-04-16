package com.example.product.application.mapper;
import com.example.product.application.dto.command.CreateProductCommand;
import com.example.product.application.dto.command.UpdateProductCommand;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.domain.model.Product;
import com.example.product.domain.model.ProductVariant;
import com.example.product.domain.model.ProductVariantAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {
    ProductResponse toResponse(Product product);

    @Mapping(target = "variantSummary", expression = "java(variant.getDetailedVariantSummary())")
    ProductResponse.ProductVariantResponse toVariantResponse(ProductVariant variant);

    ProductResponse.AttributeResponse toVariantAttributeResponse(ProductVariantAttribute variantAttribute);

    List<ProductVariant> toVariantsFormCreateCommand(List<CreateProductCommand.VariantItem> items);
    ProductVariant toVariant(CreateProductCommand.VariantItem item);
    ProductVariantAttribute toVariantAttribute(CreateProductCommand.VariantItem.AttributeItem item);

    List<ProductVariant> toVariantsFormUpdateCommand(List<UpdateProductCommand.VariantItem> items);
    ProductVariant toVariant(UpdateProductCommand.VariantItem item);
    ProductVariantAttribute toVariantAttribute(UpdateProductCommand.VariantItem.AttributeItem item);

}
