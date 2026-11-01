package com.example.demo.controller;

import com.example.demo.dto.AdminStatsResponse;
import com.example.demo.service.AdminStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    public AdminStatsController(
            AdminStatsService adminStatsService) {

        this.adminStatsService = adminStatsService;
    }

    @GetMapping
    public ResponseEntity<AdminStatsResponse> getStats() {

        return ResponseEntity.ok(
                adminStatsService.getStats()
        );
    }
}