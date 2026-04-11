package com.example.product.application.dto.response;

public record RoleResponse(
        Long id,
        String name,
        String description,
        boolean deleted
) {

}
