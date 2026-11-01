package com.example.demo.service;

import com.example.demo.dto.DailyTransactionStatsResponse;
import com.example.demo.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DailyTransactionStatsService {

    private final TransactionRepository transactionRepository;

    public DailyTransactionStatsService(
            TransactionRepository transactionRepository) {

        this.transactionRepository = transactionRepository;
    }

    public List<DailyTransactionStatsResponse> getDailyStats() {

        return transactionRepository
                .getDailyTransactionStats()
                .stream()
                .map(row -> new DailyTransactionStatsResponse(
                        (LocalDate) row[0],
                        ((Number) row[1]).longValue(),
                        (BigDecimal) row[2]
                ))
                .toList();
    }
}