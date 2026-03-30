package com.example.identity.domain.exception;

public class IdentityNotFoundException extends RuntimeException {
    public IdentityNotFoundException(String id) {
        super("Identity không tìm thấy với id: " + id);
    }
}
