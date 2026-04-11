package com.example.product.application.service;

import com.example.product.application.dto.command.RegisterAccountCommand;
import com.example.product.application.dto.response.AccountResponse;
import com.example.product.application.mapper.AccountDtoMapper;
import com.example.product.domain.model.Account;
import com.example.product.domain.model.Role;
import com.example.product.domain.repository.AccountRepository;
import com.example.product.domain.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountApplicationService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final AccountDtoMapper accountDtoMapper;
    private final PasswordEncoder passwordEncoder;

    public AccountApplicationService(AccountRepository accountRepository, RoleRepository roleRepository, AccountDtoMapper accountDtoMapper, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.accountDtoMapper = accountDtoMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public AccountResponse registerAccount(RegisterAccountCommand command) {
        if(accountRepository.exitsEmail(command.email())){
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Hệ thống chưa cấu hình ROLE_USER"));

        String encodePassword = passwordEncoder.encode(command.rawPassword());

        Account account = new Account(
            null,
                command.email(),
                encodePassword,
                userRole
        );
        Account savedAccount = accountRepository.save(account);
        return accountDtoMapper.toAccountResponse(savedAccount);
    }
}
