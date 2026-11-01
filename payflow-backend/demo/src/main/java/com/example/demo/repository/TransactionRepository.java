package com.example.demo.repository;

import com.example.demo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.example.demo.entity.TransactionType;
import org.springframework.data.repository.query.Param;
public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);
List<Transaction> findBySourceAccountOwnerIdOrDestinationAccountOwnerIdOrderByCreatedAtDesc(
        Long sourceOwnerId,
        Long destinationOwnerId);
List<Transaction> findBySourceAccountOwnerIdAndCreatedAtAfter(
        Long userId,
        LocalDateTime dateTime
);
@Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t")
BigDecimal sumAllTransactionAmounts();

@Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM Transaction t
    WHERE t.type = :type
""")
BigDecimal sumAmountByType(
        @Param("type") TransactionType type
);
@Query("""
    SELECT CAST(t.createdAt AS LocalDate),
           COUNT(t),
           COALESCE(SUM(t.amount), 0)
    FROM Transaction t
    GROUP BY CAST(t.createdAt AS LocalDate)
    ORDER BY CAST(t.createdAt AS LocalDate)
""")
List<Object[]> getDailyTransactionStats();
@Query("""
    SELECT t.status, COUNT(t)
    FROM Transaction t
    GROUP BY t.status
    ORDER BY t.status
""")
List<Object[]> getTransactionStatsByStatus();
@Query("""
    SELECT t.type, COUNT(t)
    FROM Transaction t
    GROUP BY t.type
    ORDER BY t.type
""")
List<Object[]> getTransactionStatsByType();
}
