package com.example.demo.controller;

import com.example.demo.dto.TransactionStatusStatsResponse;
import com.example.demo.service.TransactionStatusStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
public class TransactionStatusStatsController {

    private final TransactionStatusStatsService service;

    public TransactionStatusStatsController(
            TransactionStatusStatsService service) {
        this.service = service;
    }

    @GetMapping("/status")
    public ResponseEntity<List<TransactionStatusStatsResponse>> getStatsByStatus() {

        return ResponseEntity.ok(
                service.getStatsByStatus()
        );
    }
}