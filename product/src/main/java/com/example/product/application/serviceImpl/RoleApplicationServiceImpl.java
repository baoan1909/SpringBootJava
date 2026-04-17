package com.example.product.application.serviceImpl;

import com.example.product.application.dto.command.CreateRoleCommand;
import com.example.product.application.dto.response.RoleResponse;
import com.example.product.application.mapper.RoleDtoMapper;
import com.example.product.application.service.RoleApplicationService;
import com.example.product.domain.model.Role;
import com.example.product.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleApplicationServiceImpl implements RoleApplicationService {
    private final RoleRepository roleRepository;
    private final RoleDtoMapper roleDtoMapper;

    public RoleApplicationServiceImpl(RoleRepository roleRepository, RoleDtoMapper roleDtoMapper) {
        this.roleRepository = roleRepository;
        this.roleDtoMapper = roleDtoMapper;
    }

    @Override
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

    @Override
    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleDtoMapper::toRoleResponse)
                .toList();
    }
}
