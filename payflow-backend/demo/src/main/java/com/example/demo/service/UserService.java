package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;
import com.example.demo.entity.UserStatus;
import org.springframework.transaction.annotation.Transactional;
@Service
public class UserService {
     
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuditLogService auditLogService;
    private final AccountService accountService;
    public UserService(UserRepository userRepository,
                   PasswordEncoder passwordEncoder,
                   RoleRepository roleRepository,
                   AuditLogService auditLogService,
                   AccountService accountService) {

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.roleRepository = roleRepository;
    this.auditLogService = auditLogService;
    this.accountService = accountService;
}

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

public void deleteUser(Long id) {
    userRepository.deleteById(id);
}
@Transactional
 public User createUser(User user) {

    if (userRepository.existsByEmail(user.getEmail())) {
        throw new EmailAlreadyExistsException("Email already exists");
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));

    Role userRole = roleRepository.findByName("USER")
            .orElseThrow(() -> new RuntimeException("USER role not found"));

    user.setRoles(Set.of(userRole));
    user.setStatus(UserStatus.ACTIVE);

    User savedUser = userRepository.save(user);

    accountService.createAccount(savedUser.getId());

    auditLogService.createLog(
            "USER_CREATED",
            savedUser.getEmail(),
            "User account created"
    );

    return savedUser;
}

}