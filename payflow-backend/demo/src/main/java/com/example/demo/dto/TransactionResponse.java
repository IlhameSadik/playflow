package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.demo.entity.TransactionStatus;
import com.example.demo.entity.TransactionType;


public class TransactionResponse {

    private Long id;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;
    private String description;
    private TransactionType type;
private TransactionStatus status;
    private LocalDateTime createdAt;
    
    public TransactionResponse(
            Long id,
            String sourceAccountNumber,
            String destinationAccountNumber,
            BigDecimal amount,
            String description,
           TransactionType type,
        TransactionStatus status,
            LocalDateTime createdAt) {

        this.id = id;
        this.sourceAccountNumber = sourceAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
        
    }

    public Long getId() {
        return id;
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public TransactionType getType() {
        return type;
    }
}