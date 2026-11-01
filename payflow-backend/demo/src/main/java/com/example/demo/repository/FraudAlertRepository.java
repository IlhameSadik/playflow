package com.example.demo.repository;
import com.example.demo.entity.FraudAlertStatus;
import com.example.demo.entity.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FraudAlertRepository
        extends JpaRepository<FraudAlert, Long> {

List<FraudAlert> findByStatusOrderByCreatedAtDesc(
        FraudAlertStatus status
);
    List<FraudAlert>
    findByTransactionSourceAccountOwnerIdOrTransactionDestinationAccountOwnerId(
            Long sourceOwnerId,
            Long destinationOwnerId
    );

    boolean existsByTransactionIdAndReason(
            Long transactionId,
            String reason
    );
    long countByStatus(FraudAlertStatus status);
}