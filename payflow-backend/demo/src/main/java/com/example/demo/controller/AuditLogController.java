package com.example.demo.controller;

import com.example.demo.dto.AuditLogResponse;
import com.example.demo.entity.AuditLog;
import com.example.demo.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAllLogs() {

        List<AuditLogResponse> response =
                auditLogService.getAllLogs()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<AuditLogResponse>> getUserLogs(
            @PathVariable String username) {

        List<AuditLogResponse> response =
                auditLogService.getUserLogs(username)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    private AuditLogResponse toResponse(AuditLog log) {

        return new AuditLogResponse(
                log.getId(),
                log.getAction(),
                log.getUsername(),
                log.getDescription(),
                log.getCreatedAt()
        );
    }
}