package com.example.demo.controller;

import com.example.demo.dto.DailyTransactionStatsResponse;
import com.example.demo.service.DailyTransactionStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
public class DailyTransactionStatsController {

    private final DailyTransactionStatsService dailyTransactionStatsService;

    public DailyTransactionStatsController(
            DailyTransactionStatsService dailyTransactionStatsService) {

        this.dailyTransactionStatsService =
                dailyTransactionStatsService;
    }

    @GetMapping("/daily")
    public ResponseEntity<List<DailyTransactionStatsResponse>> getDailyStats() {

        return ResponseEntity.ok(
                dailyTransactionStatsService.getDailyStats()
        );
    }
}