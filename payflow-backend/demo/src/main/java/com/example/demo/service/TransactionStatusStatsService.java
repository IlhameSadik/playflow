package com.example.demo.service;

import com.example.demo.dto.TransactionStatusStatsResponse;
import com.example.demo.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionStatusStatsService {

    private final TransactionRepository transactionRepository;

    public TransactionStatusStatsService(
            TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionStatusStatsResponse> getStatsByStatus() {

        return transactionRepository
                .getTransactionStatsByStatus()
                .stream()
                .map(row -> new TransactionStatusStatsResponse(
                        row[0].toString(),
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }
}