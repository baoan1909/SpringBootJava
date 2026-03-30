package com.example.identity.infrastructure.persistence.repository;

import com.example.identity.infrastructure.persistence.entity.IdentityJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataIdentityRepository extends JpaRepository<IdentityJpaEntity, String> {
    Optional<IdentityJpaEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
