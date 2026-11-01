package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FraudAlertResponse {

    private Long id;
    private Long transactionId;
    private BigDecimal amount;
    private String reason;
    private String status;
    private LocalDateTime createdAt;

    public FraudAlertResponse(
            Long id,
            Long transactionId,
            BigDecimal amount,
            String reason,
            String status,
            LocalDateTime createdAt) {

        this.id = id;
        this.transactionId = transactionId;
        this.amount = amount;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}