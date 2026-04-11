package com.example.product.infrastructure.persistence.repository;

import com.example.product.domain.model.Role;
import com.example.product.domain.repository.RoleRepository;
import com.example.product.infrastructure.persistence.entity.RoleJpaEntity;
import com.example.product.infrastructure.persistence.mapper.RoleEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final SpringDataRoleRepository springDataRoleRepository;
    private final RoleEntityMapper roleEntityMapper;

    public RoleRepositoryImpl(SpringDataRoleRepository springDataRoleRepository, RoleEntityMapper roleEntityMapper) {
        this.springDataRoleRepository = springDataRoleRepository;
        this.roleEntityMapper = roleEntityMapper;
    }

    @Override
    public Role save(Role role) {
        RoleJpaEntity roleJpaEntity = roleEntityMapper.toRoleJpaEntity(role);
        RoleJpaEntity savedRoleJpaEntity = springDataRoleRepository.save(roleJpaEntity);
        return roleEntityMapper.toRoleDomain(savedRoleJpaEntity);
    }

    @Override
    public List<Role> findAll() {
        return springDataRoleRepository.findAll()
                .stream()
                .map(roleEntityMapper::toRoleDomain)
                .toList();
    }

    @Override
    public Optional<Role> findByName(String name) {
        return springDataRoleRepository.findByName(name).map(roleEntityMapper::toRoleDomain);
    }
}
