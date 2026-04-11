package com.example.product.interfaces.rest;

import com.example.product.application.dto.command.RegisterAccountCommand;
import com.example.product.application.dto.response.AccountResponse;
import com.example.product.application.service.AccountApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountApplicationService accountApplicationService;

    public AccountController(AccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@RequestBody RegisterAccountCommand command) {
        return accountApplicationService.registerAccount(command);
    }
}
