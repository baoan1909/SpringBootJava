package com.example.product.application.service;

import com.example.product.application.dto.command.CreateRoleCommand;
import com.example.product.application.dto.response.RoleResponse;

import java.util.List;

public interface RoleApplicationService {
    RoleResponse createRole(CreateRoleCommand command);
    List<RoleResponse> getAll();
}
