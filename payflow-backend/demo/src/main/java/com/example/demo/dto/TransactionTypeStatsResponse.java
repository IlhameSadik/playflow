package com.example.demo.dto;

public class TransactionTypeStatsResponse {

    private String type;
    private long count;

    public TransactionTypeStatsResponse(
            String type,
            long count) {

        this.type = type;
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public long getCount() {
        return count;
    }
}