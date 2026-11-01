package com.example.demo.controller;

import com.example.demo.dto.TransactionTypeStatsResponse;
import com.example.demo.service.TransactionTypeStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
public class TransactionTypeStatsController {

    private final TransactionTypeStatsService service;

    public TransactionTypeStatsController(
            TransactionTypeStatsService service) {
        this.service = service;
    }

    @GetMapping("/type")
    public ResponseEntity<List<TransactionTypeStatsResponse>> getStatsByType() {

        return ResponseEntity.ok(
                service.getStatsByType()
        );
    }
}