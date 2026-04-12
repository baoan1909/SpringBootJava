package com.example.product.application.dto.response;

public record JwtAuthResponse(
        String accessToken,
        String tokenType
) {
}
