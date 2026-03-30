package com.example.identity.application.dto;

import java.time.LocalDate;

public record UpdateIdentityCommand(
        String username,
        String password,
        String firstname,
        String lastname,
        LocalDate dob
) {
}
