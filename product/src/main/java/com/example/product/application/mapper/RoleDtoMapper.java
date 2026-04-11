package com.example.product.application.mapper;

import com.example.product.application.dto.response.RoleResponse;
import com.example.product.domain.model.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleDtoMapper {
    RoleResponse toRoleResponse(Role role);
}
