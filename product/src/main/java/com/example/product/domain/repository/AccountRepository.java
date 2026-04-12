package com.example.product.domain.repository;

import com.example.product.domain.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);
    Optional<Account> findByEmail(String email);
    boolean exitsEmail(String email);
}
