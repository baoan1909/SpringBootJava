package com.example.product.application.dto.command;

import java.util.Set;

public record CreateAccountByAdminCommand(
        String email,
        String password,
        Set<String> roles
) {
}
