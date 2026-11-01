package com.example.demo.repository;

import com.example.demo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {
//→ récupère les notifications d'un utilisateur, plus récentes en premier.
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
//→ récupère uniquement les notifications non lues.
    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);
}