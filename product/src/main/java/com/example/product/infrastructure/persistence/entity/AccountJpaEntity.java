package com.example.product.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountJpaEntity extends AuditableAggregateRootJpaEntity<AccountJpaEntity> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(name = "is_active")
    private boolean active;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_roles",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleJpaEntity> roles = new HashSet<>();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(!(obj instanceof AccountJpaEntity)) return false;
        AccountJpaEntity that = (AccountJpaEntity) obj;

        return id !=null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
