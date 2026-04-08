package com.example.product.infrastructure.persistence.repository;

import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.infrastructure.persistence.entity.ProductJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<ProductJpaEntity> filterBy(ProductCriteriaCommand command) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (command.keyWord() != null && !command.keyWord().isBlank()) {
                String pattern = "%" + command.keyWord().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), pattern);
                Predicate skuLike = cb.like(cb.lower(root.get("sku")), pattern);
                predicates.add(cb.or(nameLike, skuLike));
            }

            if (command.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), command.minPrice()));
            }

            if (command.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), command.maxPrice()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
