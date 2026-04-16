package com.example.product.infrastructure.persistence.entity;

import com.example.product.domain.model.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductJpaEntity extends AuditableJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(precision = 19, scale = 2)
    private BigDecimal minPrice;

    @Column(precision = 19, scale = 2)
    private BigDecimal maxPrice;
    private Integer totalStock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariantJpaEntity> variants = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ProductJpaEntity)) return false;
        ProductJpaEntity that = (ProductJpaEntity) obj;

        return id != null && id.equals(that.id) ;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
