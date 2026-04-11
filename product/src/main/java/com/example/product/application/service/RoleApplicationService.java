package com.example.product.application.service;

import com.example.product.application.dto.command.CreateRoleCommand;
import com.example.product.application.dto.response.RoleResponse;
import com.example.product.application.mapper.RoleDtoMapper;
import com.example.product.domain.model.Role;
import com.example.product.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleApplicationService {
    private final RoleRepository roleRepository;
    private final RoleDtoMapper roleDtoMapper;

    public RoleApplicationService(RoleRepository roleRepository, RoleDtoMapper roleDtoMapper) {
        this.roleRepository = roleRepository;
        this.roleDtoMapper = roleDtoMapper;
    }

    public RoleResponse createRole(CreateRoleCommand command) {
        String roleName = command.name().trim().toUpperCase();
        if (roleRepository.findByName(roleName).isPresent()){
            throw new IllegalArgumentException(String.format("Role với name %s đã tồn tại", roleName));
        }
        Role role = new Role(
                null,
                roleName,
                command.description()
        );
        Role savedRole =  roleRepository.save(role);
        return roleDtoMapper.toRoleResponse(savedRole);
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleDtoMapper::toRoleResponse)
                .toList();
    }
}
