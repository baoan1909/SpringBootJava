package com.example.product.infrastructure.persistence.repository;

import com.example.product.domain.model.Product;
import com.example.product.domain.repository.ProductRepository;
import com.example.product.infrastructure.persistence.entity.ProductJpaEntity;
import com.example.product.infrastructure.persistence.mapper.ProductMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductMapper productMapper;
    private final SpringDataProductRepository springDataProductRepository;

    public ProductRepositoryImpl(ProductMapper productMapper, SpringDataProductRepository springDataProductRepository) {
        this.productMapper = productMapper;
        this.springDataProductRepository = springDataProductRepository;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity productJpaEntity = productMapper.toJpaEntity(product);
        ProductJpaEntity productSaved = springDataProductRepository.save(productJpaEntity);
        return productMapper.toDomain(productSaved);
    }

    @Override
    public List<Product> findAll() {
        return springDataProductRepository.findAll()
                .stream()
                .map(productMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return springDataProductRepository.findById(id).map(productMapper::toDomain);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return springDataProductRepository.findBySku(sku).map(productMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        springDataProductRepository.deleteById(id);

    }

    @Override
    public boolean existsById(Long id) {
        return springDataProductRepository.existsById(id);
    }
}
