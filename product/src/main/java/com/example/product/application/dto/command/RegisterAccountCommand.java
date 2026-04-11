package com.example.product.application.dto.command;

public record RegisterAccountCommand(
        String email,
        String rawPassword
) {
}
