package com.example.demo.service;

import com.example.demo.dto.TransactionTypeStatsResponse;
import com.example.demo.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionTypeStatsService {

    private final TransactionRepository transactionRepository;

    public TransactionTypeStatsService(
            TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionTypeStatsResponse> getStatsByType() {

        return transactionRepository
                .getTransactionStatsByType()
                .stream()
                .map(row -> new TransactionTypeStatsResponse(
                        row[0].toString(),
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }
}