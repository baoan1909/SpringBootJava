package com.example.product.application.service;

import com.example.product.application.dto.command.RegisterAccountCommand;
import com.example.product.application.dto.response.AccountResponse;
import com.example.product.application.mapper.AccountDtoMapper;
import com.example.product.domain.model.Account;
import com.example.product.domain.model.Role;
import com.example.product.domain.repository.AccountRepository;
import com.example.product.domain.repository.RoleRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthApplicationService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountDtoMapper accountDtoMapper;

    public AuthApplicationService(AccountRepository accountRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AccountDtoMapper accountDtoMapper) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountDtoMapper = accountDtoMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với email này: "));

        Set<GrantedAuthority> authorities = account.getRoles().stream()
                .map(role -> {
                    String roleName = role.getName().toUpperCase();
                    if (!roleName.startsWith("ROLE_")) {
                        roleName = "ROLE_" + roleName;
                    }
                    return new SimpleGrantedAuthority(roleName);
                })
                .collect(Collectors.toSet());
        return new User(
                account.getEmail(),
                account.getPasswordHash(),
                account.isActive(),
                true, true, true, authorities
        );
    }

    public AccountResponse registerAccount(RegisterAccountCommand command) {
        if(accountRepository.exitsEmail(command.email())){
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Hệ thống chưa cấu hình ROLE_USER"));

        String encodePassword = passwordEncoder.encode(command.password());

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
