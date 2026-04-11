package com.example.product.domain.model;

import jakarta.persistence.Entity;
import lombok.Getter;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

@Getter
public class Role {
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
}
