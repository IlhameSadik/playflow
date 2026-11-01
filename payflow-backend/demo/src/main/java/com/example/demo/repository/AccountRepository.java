package com.example.demo.repository;

import com.example.demo.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByOwnerId(Long userId);

    boolean existsByOwnerId(Long userId);

    boolean existsByAccountNumber(String accountNumber);
@Query("""
    SELECT COALESCE(SUM(a.balance), 0)
    FROM Account a
""")
BigDecimal sumAllBalances();
}