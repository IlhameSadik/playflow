package com.example.demo.controller;

import com.example.demo.dto.NotificationResponse;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.NotificationService;
import com.example.demo.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(
            NotificationService notificationService,
            UserService userService) {

        this.notificationService = notificationService;
        this.userService = userService;
    }

    // =========================
    // GET MY NOTIFICATIONS
    // =========================

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        List<Notification> notifications =
                notificationService.getUserNotifications(user.getId());

        List<NotificationResponse> response =
                notifications.stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    // =========================
    // GET UNREAD NOTIFICATIONS
    // =========================

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        List<Notification> notifications =
                notificationService.getUnreadNotifications(user.getId());

        List<NotificationResponse> response =
                notifications.stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    // =========================
    // MARK AS READ
    // =========================

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        Notification notification =
                notificationService.markAsRead(
                        id,
                        user.getId()
                );

        return ResponseEntity.ok(toResponse(notification));
    }

    // =========================
    // GET AUTHENTICATED USER
    // =========================

    private User getAuthenticatedUser(
            Authentication authentication) {

        return userService
                .getUserByEmail(authentication.getName())
                .orElseThrow(() ->
                        new BusinessException("User not found"));
    }

    // =========================
    // CONVERT TO DTO
    // =========================

    private NotificationResponse toResponse(
            Notification notification) {

        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}