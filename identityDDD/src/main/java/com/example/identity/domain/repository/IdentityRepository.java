package com.example.identity.domain.repository;

import com.example.identity.domain.model.Identity;

import java.util.List;
import java.util.Optional;

public interface IdentityRepository {
    Identity save(Identity identity);
    List<Identity> findAll();
    Optional<Identity> findById(String id);
    Optional<Identity> findByUsername(String username);
    void deleteById(String id);
    boolean existsById(String id);
    boolean existsByUsername(String username);
}
