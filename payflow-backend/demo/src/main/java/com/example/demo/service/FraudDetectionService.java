package com.example.demo.service;
import com.example.demo.entity.FraudAlertStatus;
import com.example.demo.entity.FraudAlert;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.FraudAlertRepository;
import com.example.demo.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class FraudDetectionService {

    private final FraudAlertRepository fraudAlertRepository;
    private final TransactionRepository transactionRepository;
    public FraudDetectionService(
            FraudAlertRepository fraudAlertRepository,
            TransactionRepository transactionRepository) {

        this.fraudAlertRepository = fraudAlertRepository;
        this.transactionRepository = transactionRepository;
    }

public void analyzeTransaction(Transaction transaction) {
// ========================================================= 
//  RÈGLE 1 : Vérifier si le montant de la transaction est élevé 
// ========================================================= 

    if (transaction.getAmount()
            .compareTo(new BigDecimal("10000")) >= 0) {

        System.out.println("!!! FRAUD DETECTED !!!");

        createAlert(
                transaction,
                "High transaction amount"
        );
    }
    // ========================================================= 
    // RÈGLE 2 : Vérifier les transactions rapides 
    //  =========================================================
    Long userId = transaction.getSourceAccount()
        .getOwner()
        .getId();

LocalDateTime twoMinutesAgo =
        LocalDateTime.now().minusMinutes(2);

long recentTransactions =
        transactionRepository
                .findBySourceAccountOwnerIdAndCreatedAtAfter(
                        userId,
                        twoMinutesAgo)
                .size();

if (recentTransactions >= 3) {

    createAlert(
            transaction,
            "Multiple transactions in a short period"
    );
}
}

    private void createAlert(
        Transaction transaction,
        String reason) {

    boolean alreadyExists =
            fraudAlertRepository
                    .existsByTransactionIdAndReason(
                            transaction.getId(),
                            reason
                    );

    if (alreadyExists) {
        return;
    }

    FraudAlert alert = new FraudAlert();

    alert.setTransaction(transaction);
    alert.setReason(reason);
alert.setStatus(FraudAlertStatus.OPEN);    alert.setCreatedAt(LocalDateTime.now());

    fraudAlertRepository.save(alert);
}
    
}