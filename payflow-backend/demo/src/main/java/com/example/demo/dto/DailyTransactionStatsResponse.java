package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyTransactionStatsResponse {

    private LocalDate date;
    private long transactionCount;
    private BigDecimal totalAmount;

    public DailyTransactionStatsResponse(
            LocalDate date,
            long transactionCount,
            BigDecimal totalAmount) {

        this.date = date;
        this.transactionCount = transactionCount;
        this.totalAmount = totalAmount;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getTransactionCount() {
        return transactionCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}