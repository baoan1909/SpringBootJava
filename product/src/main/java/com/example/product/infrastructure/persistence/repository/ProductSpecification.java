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
                String pattern = "%" + command.keyWord().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), pattern);
                Predicate idLike = cb.like(root.get("id").as(String.class), pattern);

                jakarta.persistence.criteria.Join<Object, Object> variantJoin = root.join("variants", JoinType.LEFT);
                Predicate variantSkuLike = cb.like(cb.lower(variantJoin.get("skuCode")), pattern);

                predicates.add(cb.or(nameLike, idLike, variantSkuLike));

                query.distinct(true);
            }

            if (command.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("maxPrice"), command.minPrice()));
            }

            if (command.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("minPrice"), command.maxPrice()));
            }

            if(command.state() != null && !command.state().isBlank()){
                predicates.add(cb.equal(root.get("state"), command.state()));
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
