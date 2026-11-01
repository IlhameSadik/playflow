package com.example.demo.service;
import com.example.demo.exception.BusinessException;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.entity.UserStatus;
import com.example.demo.entity.Role;
import com.example.demo.entity.UserStatus;
import com.example.demo.repository.RoleRepository;
import java.time.LocalDateTime;
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final RoleRepository roleRepository;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuditLogService auditLogService,
            RoleRepository roleRepository) {

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.auditLogService = auditLogService;
    this.roleRepository = roleRepository;
}

    public User login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

System.out.println("DEBUG EMAIL : " + user.getEmail());
System.out.println("DEBUG PASSWORD MATCH : "
        + passwordEncoder.matches(password, user.getPassword()));

if (!passwordEncoder.matches(password, user.getPassword())) {
    throw new RuntimeException("Invalid email or password");
}

System.out.println("DEBUG LOGIN: mot de passe correspondant");

       if (user.getStatus() != UserStatus.ACTIVE) {
    throw new BusinessException("Account is blocked");
}
try {
    auditLogService.createLog(
        "LOGIN",
        user.getEmail(),
        "User logged in successfully"
    );
    System.out.println("DEBUG LOGIN: audit log créé");
} catch (Exception e) {
    System.out.println("DEBUG AUDIT ERROR: " + e.getMessage());
}
        return user;
    }
    public User register(
        String firstName,
        String lastName,
        String email,
        String password) {

    if (userRepository.findByEmail(email).isPresent()) {
        throw new BusinessException("Cet email est déjà utilisé.");
    }

    Role userRole = roleRepository.findAll()
            .stream()
            .filter(role -> role.getName().equals("ROLE_USER")
                    || role.getName().equals("USER"))
            .findFirst()
            .orElseThrow(() ->
                    new BusinessException("Le rôle USER n'existe pas."));

    User user = User.builder()
            .firstName(firstName.trim())
            .lastName(lastName.trim())
            .email(email.trim().toLowerCase())
            .password(passwordEncoder.encode(password))
            .status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .roles(new java.util.HashSet<>(java.util.Set.of(userRole)))
            .build();

    User savedUser = userRepository.save(user);

    auditLogService.createLog(
            "REGISTER",
            savedUser.getEmail(),
            "User registered successfully"
    );

    return savedUser;
}
}