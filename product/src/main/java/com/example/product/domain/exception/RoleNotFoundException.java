package com.example.product.domain.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(Long id) {
        super("Không tìm thấy role id: " + id);
    }
}
