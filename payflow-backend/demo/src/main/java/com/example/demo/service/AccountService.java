package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.entity.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.demo.exception.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import com.example.demo.entity.AccountStatus;
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
public AccountService(
        AccountRepository accountRepository,
        UserRepository userRepository,
        AuditLogService auditLogService) {

    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.auditLogService = auditLogService;
}

    public Account createAccount(Long userId) {

        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Vérifier qu'il n'a pas déjà un compte
        if (accountRepository.existsByOwnerId(userId)) {
            throw new RuntimeException(
                    "User already has an account");
        }

        Account account = new Account();

        // Générer un numéro de compte
        account.setAccountNumber(
                "PF-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        // Solde initial
        account.setBalance(BigDecimal.ZERO);

        // Devise
        account.setCurrency("MAD");

        // Statut
        account.setStatus(AccountStatus.ACTIVE);
        // Date de création
        account.setCreatedAt(LocalDateTime.now());

        // Propriétaire
        account.setOwner(user);

account = accountRepository.save(account);

auditLogService.createLog(
        "ACCOUNT_CREATED",
        user.getEmail(),
        "Account " + account.getAccountNumber() + " created"
);

return account;    }
    public Account getAccountByUserId(Long userId) {

    return accountRepository.findByOwnerId(userId)
            .orElseThrow(() ->
                    new RuntimeException("Account not found"));
}
public boolean isUserOwner(Long userId, String email) {

    User user = userRepository.findById(userId)
            .orElseThrow(() ->
                    new BusinessException("User not found"));

    return user.getEmail().equals(email);
}
}