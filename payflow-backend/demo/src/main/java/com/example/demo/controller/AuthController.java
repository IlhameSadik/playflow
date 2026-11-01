package com.example.demo.controller;

import jakarta.validation.Valid;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.JwtService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
     private final JwtService jwtService;
    private final AuthService authService;

   public AuthController(AuthService authService, JwtService jwtService) {
    this.authService = authService;
    this.jwtService = jwtService;
}
public record RegisterRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password
) {}

@PostMapping("/register")
public ResponseEntity<?> register(
        @Valid @RequestBody RegisterRequest request) {

    User user = authService.register(
            request.firstName(),
            request.lastName(),
            request.email(),
            request.password()
    );

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(java.util.Map.of(
                    "message", "Compte créé avec succès",
                    "email", user.getEmail()
            ));
}
 @PostMapping("/login")
public AuthResponse login(@Valid @RequestBody LoginRequest request) {

    User user = authService.login(
            request.getEmail(),
            request.getPassword()
    );

    String token = jwtService.generateToken(user.getEmail());
String role = user.getRoles()
        .stream()
        .map(r -> r.getName().trim().toUpperCase())
        .filter(name -> name.equals("ADMIN") || name.equals("ROLE_ADMIN"))
        .findFirst()
        .map(name -> "ADMIN")
        .orElseGet(() -> "USER");


            return new AuthResponse(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getStatus().name(),
        role,
        token
);

    
}
}