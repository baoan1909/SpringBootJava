package com.example.product.domain.model;

import lombok.Getter;

@Getter
public class Role {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SELLER = "SELLER";
    public static final String ROLE_CUSTOMER = "CUSTOMER";


    private Long id;
    private String name;
    private String description;
    private boolean deleted;

    public Role(Long id, String name, String description) {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Name không được để trống");
        }
        this.id = id;
        this.name = name.trim().toUpperCase();
        this.description = description;
        this.deleted = false;
    }

    public void updateInfo(String name, String description) {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Name không được để trống");
        }
            this.name = name.trim().toUpperCase();
            this.description = description;
    }

    public void markAsDeleted() {
        this.deleted = true;
    }

    private boolean isSystemDefaultRole(){
        return ROLE_ADMIN.equals(this.name) ||
                ROLE_SELLER.equals(this.name) ||
                ROLE_CUSTOMER.equals(this.name);
    }
}
