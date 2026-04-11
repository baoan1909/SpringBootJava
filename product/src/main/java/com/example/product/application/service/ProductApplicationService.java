package com.example.product.application.service;

import com.example.product.application.common.Pagination;
import com.example.product.application.dto.command.CreateProductCommand;
import com.example.product.application.dto.command.CreateVariantsCommand;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.application.dto.command.UpdateProductCommand;
import com.example.product.application.mapper.ProductDtoMapper;
import com.example.product.domain.exception.ProductNotFoundException;
import com.example.product.domain.model.Product;
import com.example.product.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductApplicationService {
    private final ProductRepository productRepository;
    private final ProductDtoMapper productDtoMapper;

    public ProductApplicationService(ProductRepository productRepository, ProductDtoMapper productDtoMapper) {
        this.productRepository = productRepository;
        this.productDtoMapper = productDtoMapper;
    }

    public ProductResponse create(CreateProductCommand command) {
        productRepository.findBySku(command.sku()).ifPresent(product -> {
            throw new IllegalArgumentException("SKU đã tồn tại");
        });

        Product product = new Product(
                null,
                command.sku(),
                command.name(),
                command.price()
        );
        for (CreateProductCommand.VariantItem item : command.variants()) {
            product.addVariant(item.colors(), item.sizes(), item.additionalPrice());
        }
        Product created = productRepository.save(product);
        return productDtoMapper.toResponse(created);
    }

    public ProductResponse createVariantToExistingProduct(Long productId, CreateVariantsCommand command) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.addVariant(command.colors(), command.sizes(), command.additionalPrice());
        Product created = productRepository.save(product);
        return productDtoMapper.toResponse(created);
    }

    public ProductResponse update(Long id, UpdateProductCommand command) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.updateInfo(command.name(), command.price());
        for (UpdateProductCommand.VariantUpdateItem item : command.variants()){
            product.updateVariantPrice(item.color(), item.size(), item.additionalPrice());
        }
        Product updated = productRepository.save(product);
        return productDtoMapper.toResponse(updated);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return productDtoMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public Pagination<ProductResponse> getAll(ProductCriteriaCommand command) {
        Pagination<Product> productPage = productRepository.findAll(command);
        List<ProductResponse> responseData = productPage.data()
                .stream()
                .map(productDtoMapper::toResponse)
                .toList();

        return new Pagination<>(
                responseData,
                productPage.currentPage(),
                productPage.totalPage(),
                productPage.totalElements()
        );
    }

    public void delete(Long id) {
        if(!productRepository.existsById(id)){
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    public void deleteProductVariant(Long productId, String color, String size) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.removeVariantsByCriteria(color, size);
        productRepository.save(product);
    }
}
