package com.example.product.infrastructure.persistence.repository;

import com.example.product.application.dto.command.ProductCriteriaCommand;
import com.example.product.infrastructure.persistence.entity.ProductJpaEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<ProductJpaEntity> filterBy(ProductCriteriaCommand command) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (command.keyWord() != null && !command.keyWord().isBlank()) {
                String keyword = command.keyWord().trim();
                String pattern = "%" + keyword.toLowerCase() + "%";

                List<Predicate> orPredicates = new ArrayList<>();
                orPredicates.add(cb.like(cb.lower(root.get("name")), pattern));
                jakarta.persistence.criteria.Join<Object, Object> variantJoin = root.join("variants", JoinType.LEFT);
                orPredicates.add(cb.like(cb.lower(variantJoin.get("skuCode")), pattern));

                try {
                    Long idSearch = Long.valueOf(keyword);
                    orPredicates.add(cb.equal(root.get("id"), idSearch));
                } catch (NumberFormatException e) {
                }
                predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
                query.distinct(true);
            }

            if (command.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("maxPrice"), command.minPrice()));
            }

            if (command.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("minPrice"), command.maxPrice()));
            }

            if(command.status() != null && !command.status().isBlank()){
                predicates.add(cb.equal(root.get("status"), command.status()));
            }
            if (command.ownerEmail() != null && !command.ownerEmail().isBlank()) {
                predicates.add(cb.equal(root.get("createdBy"), command.ownerEmail()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
