package com.example.product.application.dto.response;

import java.util.Set;

public record AccountResponse(
        Long id,
        String email,
        boolean active,
        Set<String> roles
) {
}
