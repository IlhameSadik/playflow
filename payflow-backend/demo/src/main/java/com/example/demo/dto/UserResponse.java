package com.example.demo.dto;

public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String status;

    public UserResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            String status) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
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
}