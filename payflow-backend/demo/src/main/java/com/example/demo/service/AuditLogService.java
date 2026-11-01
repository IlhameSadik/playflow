package com.example.demo.service;

import com.example.demo.entity.AuditLog;
import com.example.demo.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLog createLog(
            String action,
            String username,
            String description) {

        AuditLog log = new AuditLog();

        log.setAction(action);
        log.setUsername(username);
        log.setDescription(description);
        log.setCreatedAt(LocalDateTime.now());

        return auditLogRepository.save(log);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository
                .findAllByOrderByCreatedAtDesc();
    }

    public List<AuditLog> getUserLogs(String username) {
        return auditLogRepository
                .findByUsernameOrderByCreatedAtDesc(username);
    }
}