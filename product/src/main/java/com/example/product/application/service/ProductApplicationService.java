package com.example.product.application.service;

import com.example.product.application.common.Pagination;
import com.example.product.application.dto.command.CreateProductCommand;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.application.dto.response.ProductResponse;
import com.example.product.application.dto.command.UpdateProductCommand;
import com.example.product.application.mapper.ProductDtoMapper;
import com.example.product.domain.exception.ProductNotFoundException;
import com.example.product.domain.model.Product;
import com.example.product.domain.model.ProductVariant;
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
        Product product = new Product(
                null,
                command.name(),
                command.slug(),
                command.description()
        );
        List<ProductVariant> variants = productDtoMapper.toVariantsFormCreateCommand(command.variants());
        product.syncVariants(variants);
        Product created = productRepository.save(product);
        return productDtoMapper.toResponse(created);
    }

    public ProductResponse update(Long id, UpdateProductCommand command) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.updateInfo(command.name(), command.slug() ,command.description());

        List<ProductVariant> variants = productDtoMapper.toVariantsFormUpdateCommand(command.variants());
        product.syncVariants(variants);
        Product updated = productRepository.save(product);
        return productDtoMapper.toResponse(updated);
    }

    public void approveProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.approve();
        productRepository.save(product);
    }

    public void rejectProduct(Long id, String reason){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.reject(reason);
        productRepository.save(product);
    }

    public void deleteBySeller(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.sellerDelete();
        productRepository.save(product);
    }

    public void resubmitProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.resubmit();
        productRepository.save(product);
    }

    public void freezeProduct(Long id, String reason) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.freeze(reason);
        productRepository.save(product);
    }

    public void unfreezeProduct(Long id, String reason) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.unfreeze(reason);
        productRepository.save(product);
    }

    public void restoreProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.restore();
        productRepository.save(product);
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
}
