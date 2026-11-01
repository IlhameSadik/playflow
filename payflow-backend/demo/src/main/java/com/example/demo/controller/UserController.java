package com.example.demo.controller;

import jakarta.validation.Valid;
import com.example.demo.dto.UserRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.UserResponse;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.Authentication;
import com.example.demo.exception.BusinessException;
import org.springframework.security.access.prepost.PreAuthorize;
import com.example.demo.entity.UserStatus;
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
@PreAuthorize("hasRole('ADMIN')")
   @GetMapping
public List<UserResponse> getAllUsers() {

    return userService.getAllUsers()
            .stream()
            .map(user -> new UserResponse(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getStatus().name()
            ))
            .collect(Collectors.toList());
}
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/{id}")
public ResponseEntity<UserResponse> getUserById(
        @PathVariable Long id) {

    return userService.getUserById(id)
            .map(user -> ResponseEntity.ok(
                    new UserResponse(
                            user.getId(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail(),
                            user.getStatus().name()
                    )
            ))
            .orElse(ResponseEntity.notFound().build());
}

    @PostMapping
    public User createUser(@Valid @RequestBody UserRequest request) {

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userService.createUser(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/me")
public ResponseEntity<UserResponse> getMyProfile(
        Authentication authentication) {

    User user = userService
            .getUserByEmail(authentication.getName())
            .orElseThrow(() ->
                    new BusinessException("User not found"));

    UserResponse response = new UserResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getStatus().name()
    );

    return ResponseEntity.ok(response);
}
}