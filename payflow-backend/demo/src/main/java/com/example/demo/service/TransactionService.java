package com.example.demo.service;
import com.example.demo.exception.BusinessException;
import  com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.demo.entity.TransactionStatus;
import com.example.demo.entity.TransactionType;
import com.example.demo.entity.AccountStatus;
import org.springframework.data.domain.Sort;
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;
    private final FraudDetectionService fraudDetectionService;
    private final AuditLogService auditLogService;
public TransactionService(
        TransactionRepository transactionRepository,
        AccountRepository accountRepository,
        NotificationService notificationService,
        FraudDetectionService fraudDetectionService,
        AuditLogService auditLogService) {

    this.transactionRepository = transactionRepository;
    this.accountRepository = accountRepository;
    this.notificationService = notificationService;
    this.fraudDetectionService = fraudDetectionService;
    this.auditLogService = auditLogService;
}
    @Transactional
    public Transaction transfer(
            String userEmail,
            String sourceAccountNumber,
            String destinationAccountNumber,
            BigDecimal amount,
            String description,
            String idempotencyKey) {

        // 1. Vérifier l'idempotence
        if (transactionRepository
                .existsByIdempotencyKey(idempotencyKey)) {

           throw new BusinessException(
        "Transaction already processed");
        }

        // 2. Vérifier que le montant est positif
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                    "Amount must be greater than zero");
        }

        // 3. Récupérer le compte source
        Account source = accountRepository
                .findByAccountNumber(sourceAccountNumber)
                .orElseThrow(() ->
                        new BusinessException(
                                "Source account not found"));

        // 4. Récupérer le compte destination
        Account destination = accountRepository
                .findByAccountNumber(destinationAccountNumber)
                .orElseThrow(() ->
                        new BusinessException(
                                "Destination account not found"));
        if (source.getId().equals(destination.getId())) {
    throw new BusinessException(
            "Source and destination accounts must be different"
    );
}
                           
        // 5. Vérifier que les comptes sont actifs
        if (source.getStatus() != AccountStatus.ACTIVE ||
        destination.getStatus() != AccountStatus.ACTIVE) {

    throw new BusinessException("Account is not active");
}
if (!source.getOwner().getEmail().equals(userEmail)) {
    throw new BusinessException(
            "You are not the owner of the source account");
}
        // 6. Vérifier le solde
        if (source.getBalance().compareTo(amount) < 0) {
            throw new BusinessException(
                    "Insufficient balance");
        }

        // 7. Retirer l'argent du compte source
        source.setBalance(
                source.getBalance().subtract(amount)
        );

        // 8. Ajouter l'argent au compte destination
        destination.setBalance(
                destination.getBalance().add(amount)
        );

        // 9. Sauvegarder les comptes
        accountRepository.save(source);
        accountRepository.save(destination);

        // 10. Créer la transaction
        Transaction transaction = new Transaction();

        transaction.setSourceAccount(source);
        transaction.setDestinationAccount(destination);
        transaction.setAmount(amount);
        transaction.setDescription(description);
transaction.setType(TransactionType.TRANSFER);  
      transaction.setIdempotencyKey(idempotencyKey);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCreatedAt(LocalDateTime.now());

        // 11. Sauvegarder la transaction
        transaction = transactionRepository.save(transaction);
       auditLogService.createLog(
        "TRANSFER",
        userEmail,
        "Transfer of " + amount + " MAD from account "
                + source.getAccountNumber()
                + " to account "
                + destination.getAccountNumber()
);
        fraudDetectionService.analyzeTransaction(transaction);
        notificationService.createNotification(
        source.getOwner().getId(),
        "Transfer sent",
        "You sent " + amount + " MAD to account "
                + destination.getAccountNumber()
);

notificationService.createNotification(
        destination.getOwner().getId(),
        "Transfer received",
        "You received " + amount + " MAD from account "
                + source.getAccountNumber()
);

return transaction;
    }
    @Transactional
public Transaction deposit(
        String userEmail,
        String accountNumber,
        BigDecimal amount) {

    // 1. Vérifier le montant
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new BusinessException(
                "Amount must be greater than zero");
    }

    // 2. Chercher le compte
    Account account = accountRepository
            .findByAccountNumber(accountNumber)
            .orElseThrow(() ->
                    new BusinessException(
                            "Account not found"));
     if (!account.getOwner().getEmail().equals(userEmail)) {
    throw new BusinessException(
            "You are not the owner of this account");
}
    // 3. Vérifier que le compte est actif
if (account.getStatus() != AccountStatus.ACTIVE) {
    throw new BusinessException(
            "Account is not active");
}

    // 4. Ajouter l'argent
    account.setBalance(
            account.getBalance().add(amount)
    );

    // 5. Sauvegarder le compte
    accountRepository.save(account);

    // 6. Créer la transaction
    Transaction transaction = new Transaction();

    transaction.setSourceAccount(account);
    transaction.setDestinationAccount(account);
    transaction.setAmount(amount);
    transaction.setDescription("Deposit");
    transaction.setType(TransactionType.DEPOSIT);
    transaction.setIdempotencyKey(
            "DEPOSIT-" + System.currentTimeMillis()
    );
    transaction.setStatus(TransactionStatus.COMPLETED);
    transaction.setCreatedAt(LocalDateTime.now());
    

    // 7. Sauvegarder la transaction
    transaction = transactionRepository.save(transaction);
     auditLogService.createLog(
        "DEPOSIT",
        userEmail,
        "Deposit of " + amount + " MAD into account "
                + account.getAccountNumber()
);
notificationService.createNotification(
        account.getOwner().getId(),
        "Deposit received",
        "You received " + amount + " MAD in your account."
);

return transaction;
}
public List<Transaction> getUserTransactions(Long userId) {

    return transactionRepository
            .findBySourceAccountOwnerIdOrDestinationAccountOwnerIdOrderByCreatedAtDesc(
                    userId,
                    userId
            );
}
public Transaction getTransactionById(Long id) {

    return transactionRepository.findById(id)
            .orElseThrow(() ->
                    new BusinessException("Transaction not found"));
}
@Transactional
public Transaction withdraw(
        String userEmail,
        String accountNumber,
        BigDecimal amount) {

    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new BusinessException(
                "Amount must be greater than zero");
    }

    Account account = accountRepository
            .findByAccountNumber(accountNumber)
            .orElseThrow(() ->
                    new BusinessException("Account not found"));

    if (!account.getOwner().getEmail().equals(userEmail)) {
        throw new BusinessException(
                "You are not the owner of this account");
    }

if (account.getStatus() != AccountStatus.ACTIVE) {
        throw new BusinessException(
                "Account is not active");
    }

    if (account.getBalance().compareTo(amount) < 0) {
        throw new BusinessException(
                "Insufficient balance");
    }

    account.setBalance(
            account.getBalance().subtract(amount)
    );

    accountRepository.save(account);

    Transaction transaction = new Transaction();

transaction.setSourceAccount(account);
transaction.setDestinationAccount(account);
transaction.setAmount(amount);
transaction.setDescription("Withdrawal");
transaction.setType(TransactionType.WITHDRAWAL);
transaction.setIdempotencyKey(
        "WITHDRAW-" + System.currentTimeMillis()
);
transaction.setStatus(TransactionStatus.COMPLETED);
transaction.setCreatedAt(LocalDateTime.now());

transaction = transactionRepository.save(transaction);
auditLogService.createLog(
        "WITHDRAWAL",
        userEmail,
        "Withdrawal of " + amount + " MAD from account "
                + account.getAccountNumber()
);
notificationService.createNotification(
        account.getOwner().getId(),
        "Withdrawal completed",
        "You withdrew " + amount + " MAD from your account."
);

return transaction;
}
public List<Transaction> getAllTransactions() {
    return transactionRepository.findAll(
        Sort.by(Sort.Direction.DESC, "createdAt")
    );
}

public List<Transaction> filterTransactions(
        String type,
        String status,
        String search) {

    return getAllTransactions().stream()
            .filter(transaction ->
                    type == null || type.isBlank()
                    || transaction.getType().name()
                        .equalsIgnoreCase(type))
            .filter(transaction ->
                    status == null || status.isBlank()
                    || transaction.getStatus().name()
                        .equalsIgnoreCase(status))
            .filter(transaction -> {
                if (search == null || search.isBlank()) {
                    return true;
                }

                String value = search.toLowerCase();

                return String.valueOf(transaction.getId())
                            .contains(value)
                        || transaction.getSourceAccount()
                            .getAccountNumber()
                            .toLowerCase().contains(value)
                        || transaction.getDestinationAccount()
                            .getAccountNumber()
                            .toLowerCase().contains(value);
            })
            .toList();
}

}