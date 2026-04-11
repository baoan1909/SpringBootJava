package com.example.product.interfaces.rest;

import com.example.product.application.dto.command.CreateRoleCommand;
import com.example.product.application.dto.response.RoleResponse;
import com.example.product.application.service.RoleApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleApplicationService roleApplicationService;

    public RoleController(RoleApplicationService roleApplicationService) {
        this.roleApplicationService = roleApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse create(@RequestBody CreateRoleCommand command) {
        return roleApplicationService.createRole(command);
    }

    @GetMapping
    public List<RoleResponse> getAll() {
        return roleApplicationService.getAll();
    }
}
