package com.example.product.infrastructure.persistence.repository;

import com.example.product.application.common.Pagination;
import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.domain.model.Product;
import com.example.product.domain.repository.ProductRepository;
import com.example.product.infrastructure.persistence.entity.ProductJpaEntity;
import com.example.product.infrastructure.persistence.mapper.ProductEntityMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductEntityMapper productEntityMapper;
    private final SpringDataProductRepository springDataProductRepository;

    public ProductRepositoryImpl(ProductEntityMapper productEntityMapper, SpringDataProductRepository springDataProductRepository) {
        this.productEntityMapper = productEntityMapper;
        this.springDataProductRepository = springDataProductRepository;
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            ProductJpaEntity productJpaEntity = productEntityMapper.toJpaEntity(product);
            ProductJpaEntity productSaved = springDataProductRepository.save(productJpaEntity);
            return productEntityMapper.toDomain(productSaved);
        }else{
            ProductJpaEntity productJpaEntity = springDataProductRepository.findById(product.getId())
                    .orElseThrow();
            productEntityMapper.updateJpaEntityDomain(product, productJpaEntity);
            ProductJpaEntity productSaved = springDataProductRepository.save(productJpaEntity);
            return productEntityMapper.toDomain(productSaved);
        }
    }

    @Override
    public Pagination<Product> findAll(ProductCriteriaCommand command) {
        Pageable pageable = PageRequest.of(command.page(), command.size());

        Specification<ProductJpaEntity> productJpaEntitySpecification = ProductSpecification.filterBy(command);

        Page<ProductJpaEntity> productJpaEntityPage = springDataProductRepository.findAll(productJpaEntitySpecification, pageable);

        List<Product> products = productJpaEntityPage.getContent()
                .stream()
                .map(productEntityMapper::toDomain)
                .toList();
        return new Pagination<>(
                products,
                productJpaEntityPage.getNumber(),
                productJpaEntityPage.getTotalPages(),
                productJpaEntityPage.getTotalElements()
        );
    }

    @Override
    public Optional<Product> findById(Long id) {
        return springDataProductRepository.findById(id).map(productEntityMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        springDataProductRepository.deleteById(id);

    }

    @Override
    public void deleteAll() {
        springDataProductRepository.deleteAll();
    }


    @Override
    public boolean existsById(Long id) {
        return springDataProductRepository.existsById(id);
    }
}
