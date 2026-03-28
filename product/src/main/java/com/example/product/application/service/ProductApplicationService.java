package com.example.product.application.service;

import com.example.product.application.dto.CreateProductCommand;
import com.example.product.application.dto.ProductResponse;
import com.example.product.application.dto.UpdateProductCommand;
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

    public ProductApplicationService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
        productRepository.save(product);
        return toResponse(product);
    }

    public ProductResponse update(Long id, UpdateProductCommand command) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));


        product.updateInfo(command.name(), command.price());
        Product updated = productRepository.save(product);
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        if(!productRepository.existsById(id)){
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getPrice()
        );
    }

}
