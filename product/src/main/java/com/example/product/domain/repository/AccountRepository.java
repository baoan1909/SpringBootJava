package com.example.product.domain.repository;

import com.example.product.domain.model.Account;

import java.util.List;

public interface AccountRepository {
    Account save(Account account);
    boolean exitsEmail(String email);
}
