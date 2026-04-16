package com.example.product.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "product_variant_attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantAttributeJpaEntity extends AuditableJpaEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attribute_name", nullable = false)
    private String name;

    @Column(name = "attribute_value", nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariantJpaEntity variant;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof ProductVariantAttributeJpaEntity)) return false;
        ProductVariantAttributeJpaEntity that = (ProductVariantAttributeJpaEntity) obj;
        if (this.id != null) {
            return this.id.equals(that.id);
        }

        return this.name != null && this.name.equals(that.name)
                && this.value != null && this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return id != null ? this.getClass().hashCode() : Objects.hash(name, value);
    }
}
