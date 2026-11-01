package com.example.demo.dto;

public class TransactionStatusStatsResponse {

    private String status;
    private long count;

    public TransactionStatusStatsResponse(
            String status,
            long count) {

        this.status = status;
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public long getCount() {
        return count;
    }
}