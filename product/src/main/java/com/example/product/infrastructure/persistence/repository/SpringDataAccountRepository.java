package com.example.product.infrastructure.persistence.repository;

import com.example.product.domain.model.Account;
import com.example.product.infrastructure.persistence.entity.AccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataAccountRepository extends JpaRepository<AccountJpaEntity, Long> {

    boolean existsByEmail(String email);
    Optional<AccountJpaEntity> findByEmail(String email);
}
