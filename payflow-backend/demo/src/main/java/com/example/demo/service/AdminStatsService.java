package com.example.demo.service;

import com.example.demo.dto.AdminStatsResponse;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.FraudAlertRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.entity.FraudAlertStatus;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import com.example.demo.entity.TransactionType;
import com.example.demo.entity.UserStatus;
@Service
public class AdminStatsService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final FraudAlertRepository fraudAlertRepository;

    public AdminStatsService(
            UserRepository userRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            FraudAlertRepository fraudAlertRepository) {

        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.fraudAlertRepository = fraudAlertRepository;
    }

    public AdminStatsResponse getStats() {

        long totalUsers =
                userRepository.count();

        long totalAccounts =
                accountRepository.count();

        long totalTransactions =
                transactionRepository.count();

       long openFraudAlerts =
        fraudAlertRepository
                .countByStatus(FraudAlertStatus.OPEN);
       
    BigDecimal totalTransactionAmount =
        transactionRepository.sumAllTransactionAmounts();
        BigDecimal totalTransferAmount =
        transactionRepository.sumAmountByType(
                TransactionType.TRANSFER);

BigDecimal totalDepositAmount =
        transactionRepository.sumAmountByType(
                TransactionType.DEPOSIT);

BigDecimal totalWithdrawalAmount =
        transactionRepository.sumAmountByType(
                TransactionType.WITHDRAWAL);
                BigDecimal totalBalance =
        accountRepository.sumAllBalances();
long activeUsers = userRepository.countByStatus(UserStatus.ACTIVE);
return new AdminStatsResponse(
        totalUsers,
        totalAccounts,
        totalTransactions,
        openFraudAlerts,
        totalTransactionAmount,
        totalTransferAmount,
        totalDepositAmount,
        totalWithdrawalAmount,
        totalBalance,
        activeUsers
);
    }
}