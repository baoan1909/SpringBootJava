package com.example.product.application.service;

import com.example.product.application.dto.command.CreateAccountByAdminCommand;
import com.example.product.application.dto.response.AccountResponse;
import com.example.product.application.mapper.AccountDtoMapper;
import com.example.product.domain.model.Account;
import com.example.product.domain.model.Role;
import com.example.product.domain.repository.AccountRepository;
import com.example.product.domain.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

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

    public AccountResponse createAccountByAdmin(CreateAccountByAdminCommand command) {
        if(accountRepository.exitsEmail(command.email())){
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        String encodePassword = passwordEncoder.encode(command.password());
        Set<Role> roles = command.roles().stream()
                .map(name ->{
                    String dbRoleName = name.toUpperCase();
                    if(dbRoleName.startsWith("ROLE_")){
                        dbRoleName = dbRoleName.substring(5);
                    }
                    return roleRepository.findByName(dbRoleName)
                            .orElseThrow(() -> new IllegalStateException("Quyền " + name + " không tồn tại"));
                })
                .collect(Collectors.toSet());
        Account account = new Account(
                null,
                command.email(),
                encodePassword,
                null
        );
        roles.forEach(account::assignRole);
        Account savedAccount = accountRepository.save(account);
        return accountDtoMapper.toAccountResponse(savedAccount);
    }
}
