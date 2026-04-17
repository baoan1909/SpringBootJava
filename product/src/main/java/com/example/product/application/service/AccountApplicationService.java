package com.example.product.application.service;

import com.example.product.application.dto.command.CreateAccountByAdminCommand;
import com.example.product.application.dto.command.RegisterAccountCommand;
import com.example.product.application.dto.response.AccountResponse;

public interface AccountApplicationService {
    AccountResponse registerAccount(RegisterAccountCommand command);
    AccountResponse createAccountByAdmin(CreateAccountByAdminCommand command);
}
