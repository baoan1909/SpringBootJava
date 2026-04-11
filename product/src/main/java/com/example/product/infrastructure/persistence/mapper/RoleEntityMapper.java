package com.example.product.infrastructure.persistence.mapper;

import com.example.product.domain.model.Account;
import com.example.product.domain.model.Role;
import com.example.product.infrastructure.persistence.entity.RoleJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleEntityMapper {
    RoleJpaEntity toRoleJpaEntity(Role role);
    Role toRoleDomain (RoleJpaEntity roleJpaEntity);
}
