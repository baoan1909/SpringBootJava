package com.example.identity.domain.model;

import java.time.LocalDate;

public class Identity {
    private final String id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private LocalDate dob;

    public Identity(String id, String username, String password, String firstname, String lastname, LocalDate dob) {

        if(username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        if(password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password không được để trống ");
        }
        if(firstname == null || firstname.isBlank()) {
            throw new IllegalArgumentException("firstname không được để trống");
        }
        if(lastname == null || lastname.isBlank()) {
            throw new IllegalArgumentException("lastname không được để trống");
        }
        if(dob == null) {
            throw new IllegalArgumentException("dob không được để trống");
        }

        this.id = id;
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
    }

    public void updateInfo(String username, String password, String firstname, String lastname, LocalDate dob) {

        if(username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username Không được để trống");
        }
        if(password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password không được để trống ");
        }
        if(firstname == null || firstname.isBlank()) {
            throw new IllegalArgumentException("firstname không được để trống");
        }
        if(lastname == null || lastname.isBlank()) {
            throw new IllegalArgumentException("lastname không được để trống");
        }
        if(dob == null) {
            throw new IllegalArgumentException("dob không được để trống");
        }

        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
}
