package com.example.demo.dto;
import java.math.BigDecimal;
public class AdminStatsResponse {

    private long totalUsers;
    private long totalAccounts;
    private long totalTransactions;
    private long openFraudAlerts;
    private BigDecimal totalTransferAmount;
    private BigDecimal totalDepositAmount;
    private BigDecimal totalWithdrawalAmount;
    private BigDecimal totalTransactionAmount;
    private BigDecimal totalBalance;
    private long activeUsers;
public AdminStatsResponse(
        long totalUsers,
        long totalAccounts,
        long totalTransactions,
        long openFraudAlerts,
        BigDecimal totalTransactionAmount,
        BigDecimal totalTransferAmount,
        BigDecimal totalDepositAmount,
        BigDecimal totalWithdrawalAmount,
        BigDecimal totalBalance,
        long activeUsers) {

    this.totalUsers = totalUsers;
    this.totalAccounts = totalAccounts;
    this.totalTransactions = totalTransactions;
    this.openFraudAlerts = openFraudAlerts;
    this.totalTransactionAmount = totalTransactionAmount;
    this.totalTransferAmount = totalTransferAmount;
    this.totalDepositAmount = totalDepositAmount;
    this.totalWithdrawalAmount = totalWithdrawalAmount;
    this.totalBalance = totalBalance;
    this.activeUsers = activeUsers;
}

    public long getTotalUsers() {
        return totalUsers;
    }
public BigDecimal getTotalBalance() {
    return totalBalance;
}
    public long getTotalAccounts() {
        return totalAccounts;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public long getOpenFraudAlerts() {
        return openFraudAlerts;
    }

    public BigDecimal getTotalTransactionAmount() {
        return totalTransactionAmount;
    }
    public BigDecimal getTotalTransferAmount() {
    return totalTransferAmount;
}

public BigDecimal getTotalDepositAmount() {
    return totalDepositAmount;
}

public BigDecimal getTotalWithdrawalAmount() {
    return totalWithdrawalAmount;
}
public long getActiveUsers() {
    return activeUsers;
}
}