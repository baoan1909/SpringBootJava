package com.example.product.application.dto.command;

public record LoginAccountCommand(
        String email,
        String password
) {
}
