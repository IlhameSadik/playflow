package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.demo.entity.UserStatus;
import java.util.List;

@Service
public class AdminUserService {

  private final UserRepository userRepository;
private final AuditLogService auditLogService;
private final NotificationService notificationService;

public AdminUserService(
        UserRepository userRepository,
        AuditLogService auditLogService,
        NotificationService notificationService) {

    this.userRepository = userRepository;
    this.auditLogService = auditLogService;
    this.notificationService = notificationService;
}
   public List<User> getUsersByStatus(String status) {

    UserStatus userStatus;

    try {
        userStatus = UserStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException e) {
        throw new BusinessException(
                "Status must be ACTIVE or BLOCKED"
        );
    }

    return userRepository.findByStatus(userStatus);
}

    public User updateStatus(
        Long userId,
        String status,
        String adminUsername) {

    User user = userRepository.findById(userId)
            .orElseThrow(() ->
                    new BusinessException("User not found"));

  UserStatus newStatus;

try {
    newStatus = UserStatus.valueOf(status.toUpperCase());
} catch (IllegalArgumentException e) {
    throw new BusinessException(
            "Status must be ACTIVE or BLOCKED");
}


 if (user.getEmail().equals(adminUsername)) {
    throw new BusinessException(
            "You cannot change your own status"
    );
}
user.setStatus(newStatus);
    User updatedUser = userRepository.save(user);
notificationService.createNotification(
        user.getId(),
        newStatus == UserStatus.BLOCKED
                ? "Account blocked"
                : "Account activated",
        newStatus == UserStatus.BLOCKED
                ? "Your account has been blocked by an administrator."
                : "Your account has been activated."
);
    auditLogService.createLog(
            "USER_STATUS_CHANGED",
            adminUsername,
            "User " + user.getEmail()
                    + " status changed to "
                    + status
    );

    return updatedUser;
}
}