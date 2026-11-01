package com.example.demo.dto;


public class AuthResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private String role;
    private String token;

    public AuthResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            String status,
            String role,
            String token) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
        this.role = role;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }
}
