package com.example.demo.dto;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private Long id;
    private String action;
    private String username;
    private String description;
    private LocalDateTime createdAt;

    public AuditLogResponse(
            Long id,
            String action,
            String username,
            String description,
            LocalDateTime createdAt) {

        this.id = id;
        this.action = action;
        this.username = username;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getUsername() {
        return username;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}