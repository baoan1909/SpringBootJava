package com.example.product.infrastructure.persistence.repository;

import com.example.product.infrastructure.persistence.entity.AccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAccountRepository extends JpaRepository<AccountJpaEntity, Long> {

    boolean existsByEmail(String email);
}
