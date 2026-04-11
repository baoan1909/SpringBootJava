package com.example.product.domain.exception;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(Long id) {
        super("Account không tìm thấy ới id: " + id);
    }
}
