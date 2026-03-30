package com.example.identity.application.dto;

import java.time.LocalDate;

public record CreateIdentityCommand(
        String username,
        String password,
        String firstname,
        String lastname,
        LocalDate dob
) {
}
