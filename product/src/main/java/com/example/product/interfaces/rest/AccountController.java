package com.example.product.interfaces.rest;

import com.example.product.application.dto.command.CreateAccountByAdminCommand;
import com.example.product.application.dto.response.AccountResponse;
import com.example.product.application.service.AccountApplicationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/accounts")
public class AccountController {
    private final AccountApplicationService accountApplicationService;

    public AccountController(AccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public AccountResponse createAccount(@RequestBody CreateAccountByAdminCommand command) {
        return accountApplicationService.createAccountByAdmin(command);
    }
}
