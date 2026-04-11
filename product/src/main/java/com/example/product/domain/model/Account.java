package com.example.product.domain.model;

import com.example.product.domain.event.AccountRegisteredEvent;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Account extends AbstractAggregateRoot<Account> {
    private Long id;
    private String email;
    private String passwordHash;
    private boolean active;

    private Set<Role> roles = new HashSet<>();

    public Account(Long id, String email, String passwordHash, Role defaultRole) {
        if(email == null || email.isBlank()){
            throw new IllegalArgumentException("Email không được để trống");
        }
        if(passwordHash == null || passwordHash.isBlank()){
            throw new IllegalArgumentException("Password Không được để trống");
        }

        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.active = true;
        if (defaultRole != null) {
            this.roles.add(defaultRole);
        }

        registerEvent(new AccountRegisteredEvent(this.email));
    }

    public void assignRole(Role role) {
        if (role == null) throw new IllegalArgumentException("Role không được để trống");
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void lockAccount() {
        this.active = false;
    }
}
