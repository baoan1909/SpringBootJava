package com.example.product.domain.repository;

import com.example.product.domain.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    Role save(Role role);
    List<Role> findAll();
    Optional<Role> findByName(String name);
}
