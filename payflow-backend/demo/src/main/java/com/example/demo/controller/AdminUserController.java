package com.example.demo.controller;

import com.example.demo.dto.AdminUserResponse;
import com.example.demo.entity.User;
import com.example.demo.service.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.Authentication;
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(
            AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> getUsers(
            @RequestParam(required = false) String status) {

        List<User> users;

        if (status == null || status.isBlank()) {
            users = adminUserService.getUsersByStatus("ACTIVE");
        } else {
            users = adminUserService.getUsersByStatus(status);
        }

        List<AdminUserResponse> response = users.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

@PutMapping("/{id}/status")
public ResponseEntity<AdminUserResponse> updateStatus(
        @PathVariable Long id,
        @RequestParam String status,
        Authentication authentication) {

    String adminUsername = authentication.getName();

    User user = adminUserService.updateStatus(
            id,
            status,
            adminUsername
    );

    return ResponseEntity.ok(toResponse(user));
}

    private AdminUserResponse toResponse(User user) {

        return new AdminUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getStatus().name(),
                user.getCreatedAt()
        );
    }

    @DeleteMapping("/{id}")
public ResponseEntity<Void> deleteUser(
        @PathVariable Long id,
        Authentication authentication) {

    adminUserService.deleteUser(id, authentication.getName());

    return ResponseEntity.noContent().build();
}
}