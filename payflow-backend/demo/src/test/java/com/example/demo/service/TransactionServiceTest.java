package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.entity.AccountStatus;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.TransactionStatus;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private FraudDetectionService fraudDetectionService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void transfer_shouldCompleteSuccessfully() {

        // Arrange
        Account source = new Account();
        source.setId(1L);
        source.setAccountNumber("PF-SOURCE");
        source.setBalance(new BigDecimal("100.00"));
        source.setStatus(AccountStatus.ACTIVE);

        Account destination = new Account();
        destination.setId(2L);
        destination.setAccountNumber("PF-DEST");
        destination.setBalance(new BigDecimal("50.00"));
        destination.setStatus(AccountStatus.ACTIVE);

        // Owners
        com.example.demo.entity.User sourceUser =
                new com.example.demo.entity.User();
        sourceUser.setId(10L);
        sourceUser.setEmail("user@test.com");

        com.example.demo.entity.User destinationUser =
                new com.example.demo.entity.User();
        destinationUser.setId(20L);
        destinationUser.setEmail("dest@test.com");

        source.setOwner(sourceUser);
        destination.setOwner(destinationUser);

        when(transactionRepository.existsByIdempotencyKey("TEST-001"))
                .thenReturn(false);

        when(accountRepository.findByAccountNumber("PF-SOURCE"))
                .thenReturn(Optional.of(source));

        when(accountRepository.findByAccountNumber("PF-DEST"))
                .thenReturn(Optional.of(destination));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Transaction result = transactionService.transfer(
                "user@test.com",
                "PF-SOURCE",
                "PF-DEST",
                new BigDecimal("30.00"),
                "Test transfer",
                "TEST-001"
        );

        // Assert
        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());

        assertEquals(
                new BigDecimal("70.00"),
                source.getBalance()
        );

        assertEquals(
                new BigDecimal("80.00"),
                destination.getBalance()
        );

        assertEquals(
                new BigDecimal("30.00"),
                result.getAmount()
        );

        verify(accountRepository).save(source);
        verify(accountRepository).save(destination);
        verify(transactionRepository).save(any(Transaction.class));

        verify(notificationService, times(2))
                .createNotification(anyLong(), anyString(), anyString());

        verify(auditLogService)
                .createLog(
                        eq("TRANSFER"),
                        eq("user@test.com"),
                        anyString()
                );

        verify(fraudDetectionService)
                .analyzeTransaction(any(Transaction.class));
    }
    @Test
void transfer_shouldFail_whenInsufficientBalance() {

    Account source = new Account();
    source.setId(1L);
    source.setAccountNumber("PF-SOURCE");
    source.setBalance(new BigDecimal("20.00"));
    source.setStatus(AccountStatus.ACTIVE);

    Account destination = new Account();
    destination.setId(2L);
    destination.setAccountNumber("PF-DEST");
    destination.setBalance(new BigDecimal("50.00"));
    destination.setStatus(AccountStatus.ACTIVE);

    com.example.demo.entity.User sourceUser =
            new com.example.demo.entity.User();
    sourceUser.setId(10L);
    sourceUser.setEmail("user@test.com");

    source.setOwner(sourceUser);

    when(transactionRepository.existsByIdempotencyKey("TEST-002"))
            .thenReturn(false);

    when(accountRepository.findByAccountNumber("PF-SOURCE"))
            .thenReturn(Optional.of(source));

    when(accountRepository.findByAccountNumber("PF-DEST"))
            .thenReturn(Optional.of(destination));

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.transfer(
                    "user@test.com",
                    "PF-SOURCE",
                    "PF-DEST",
                    new BigDecimal("100.00"),
                    "Test insufficient balance",
                    "TEST-002"
            )
    );

    assertEquals(
            "Insufficient balance",
            exception.getMessage()
    );

    verify(transactionRepository, never())
            .save(any(Transaction.class));

    verify(accountRepository, never())
            .save(any(Account.class));
}
@Test
void transfer_shouldFail_whenIdempotencyKeyAlreadyExists() {

    when(transactionRepository.existsByIdempotencyKey("TEST-003"))
            .thenReturn(true);

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.transfer(
                    "user@test.com",
                    "PF-SOURCE",
                    "PF-DEST",
                    new BigDecimal("30.00"),
                    "Duplicate transfer",
                    "TEST-003"
            )
    );

    assertEquals(
            "Transaction already processed",
            exception.getMessage()
    );

    verify(accountRepository, never())
            .findByAccountNumber(anyString());

    verify(transactionRepository, never())
            .save(any(Transaction.class));
}
@Test
void transfer_shouldFail_whenUserIsNotSourceAccountOwner() {

    Account source = new Account();
    source.setId(1L);
    source.setAccountNumber("PF-SOURCE");
    source.setBalance(new BigDecimal("100.00"));
    source.setStatus(AccountStatus.ACTIVE);

    Account destination = new Account();
    destination.setId(2L);
    destination.setAccountNumber("PF-DEST");
    destination.setBalance(new BigDecimal("50.00"));
    destination.setStatus(AccountStatus.ACTIVE);

    com.example.demo.entity.User owner =
            new com.example.demo.entity.User();
    owner.setId(10L);
    owner.setEmail("owner@test.com");

    source.setOwner(owner);

    when(transactionRepository.existsByIdempotencyKey("TEST-004"))
            .thenReturn(false);

    when(accountRepository.findByAccountNumber("PF-SOURCE"))
            .thenReturn(Optional.of(source));

    when(accountRepository.findByAccountNumber("PF-DEST"))
            .thenReturn(Optional.of(destination));

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.transfer(
                    "hacker@test.com",
                    "PF-SOURCE",
                    "PF-DEST",
                    new BigDecimal("30.00"),
                    "Unauthorized transfer",
                    "TEST-004"
            )
    );

    assertEquals(
            "You are not the owner of the source account",
            exception.getMessage()
    );

    verify(transactionRepository, never())
            .save(any(Transaction.class));

    verify(accountRepository, never())
            .save(any(Account.class));
}
@Test
void transfer_shouldFail_whenSourceAccountIsBlocked() {

    Account source = new Account();
    source.setId(1L);
    source.setAccountNumber("PF-SOURCE");
    source.setBalance(new BigDecimal("100.00"));
    source.setStatus(AccountStatus.BLOCKED);

    Account destination = new Account();
    destination.setId(2L);
    destination.setAccountNumber("PF-DEST");
    destination.setBalance(new BigDecimal("50.00"));
    destination.setStatus(AccountStatus.ACTIVE);

    com.example.demo.entity.User sourceUser =
            new com.example.demo.entity.User();
    sourceUser.setId(10L);
    sourceUser.setEmail("user@test.com");

    source.setOwner(sourceUser);

    when(transactionRepository.existsByIdempotencyKey("TEST-005"))
            .thenReturn(false);

    when(accountRepository.findByAccountNumber("PF-SOURCE"))
            .thenReturn(Optional.of(source));

    when(accountRepository.findByAccountNumber("PF-DEST"))
            .thenReturn(Optional.of(destination));

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.transfer(
                    "user@test.com",
                    "PF-SOURCE",
                    "PF-DEST",
                    new BigDecimal("30.00"),
                    "Blocked account",
                    "TEST-005"
            )
    );

    assertEquals(
            "Account is not active",
            exception.getMessage()
    );

    verify(transactionRepository, never())
            .save(any(Transaction.class));

    verify(accountRepository, never())
            .save(any(Account.class));
}
@Test
void transfer_shouldFail_whenAmountIsInvalid() {

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.transfer(
                    "user@test.com",
                    "PF-SOURCE",
                    "PF-DEST",
                    BigDecimal.ZERO,
                    "Invalid amount",
                    "TEST-006"
            )
    );

    assertEquals(
            "Amount must be greater than zero",
            exception.getMessage()
    );

    verify(transactionRepository, never())
            .save(any(Transaction.class));

    verify(accountRepository, never())
            .findByAccountNumber(anyString());
}
@Test
void transfer_shouldFail_whenSourceAndDestinationAreSame() {

    Account account = new Account();
    account.setId(1L);
    account.setAccountNumber("PF-SAME");
    account.setBalance(new BigDecimal("100.00"));
    account.setStatus(AccountStatus.ACTIVE);

    com.example.demo.entity.User user =
            new com.example.demo.entity.User();
    user.setId(10L);
    user.setEmail("user@test.com");

    account.setOwner(user);

    when(transactionRepository.existsByIdempotencyKey("TEST-007"))
            .thenReturn(false);

    when(accountRepository.findByAccountNumber("PF-SAME"))
            .thenReturn(Optional.of(account));

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.transfer(
                    "user@test.com",
                    "PF-SAME",
                    "PF-SAME",
                    new BigDecimal("30.00"),
                    "Same account",
                    "TEST-007"
            )
    );

    assertEquals(
            "Source and destination accounts must be different",
            exception.getMessage()
    );

    verify(transactionRepository, never())
            .save(any(Transaction.class));

    verify(accountRepository, never())
            .save(any(Account.class));
}
@Test
void transfer_shouldFail_whenSourceAccountNotFound() {

    when(transactionRepository.existsByIdempotencyKey("TEST-008"))
            .thenReturn(false);

    when(accountRepository.findByAccountNumber("PF-SOURCE"))
            .thenReturn(Optional.empty());

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.transfer(
                    "user@test.com",
                    "PF-SOURCE",
                    "PF-DEST",
                    new BigDecimal("30.00"),
                    "Account not found",
                    "TEST-008"
            )
    );

    assertEquals(
            "Source account not found",
            exception.getMessage()
    );

    verify(transactionRepository, never())
            .save(any(Transaction.class));

    verify(accountRepository, never())
            .save(any(Account.class));
}
@Test
void transfer_shouldFail_whenDestinationAccountNotFound() {

    Account source = new Account();
    source.setId(1L);
    source.setAccountNumber("PF-SOURCE");
    source.setBalance(new BigDecimal("100.00"));
    source.setStatus(AccountStatus.ACTIVE);

    com.example.demo.entity.User user =
            new com.example.demo.entity.User();
    user.setId(10L);
    user.setEmail("user@test.com");

    source.setOwner(user);

    when(transactionRepository.existsByIdempotencyKey("TEST-009"))
            .thenReturn(false);

    when(accountRepository.findByAccountNumber("PF-SOURCE"))
            .thenReturn(Optional.of(source));

    when(accountRepository.findByAccountNumber("PF-DEST"))
            .thenReturn(Optional.empty());

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.transfer(
                    "user@test.com",
                    "PF-SOURCE",
                    "PF-DEST",
                    new BigDecimal("30.00"),
                    "Destination not found",
                    "TEST-009"
            )
    );

    assertEquals(
            "Destination account not found",
            exception.getMessage()
    );

    verify(transactionRepository, never())
            .save(any(Transaction.class));

    verify(accountRepository, never())
            .save(any(Account.class));
}
@Test
void deposit_shouldCompleteSuccessfully() {

    Account account = new Account();
    account.setId(1L);
    account.setAccountNumber("PF-ACCOUNT");
    account.setBalance(new BigDecimal("100.00"));
    account.setStatus(AccountStatus.ACTIVE);

    com.example.demo.entity.User user =
            new com.example.demo.entity.User();
    user.setId(10L);
    user.setEmail("user@test.com");

    account.setOwner(user);

    when(accountRepository.findByAccountNumber("PF-ACCOUNT"))
            .thenReturn(Optional.of(account));

    when(transactionRepository.save(any(Transaction.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    Transaction result = transactionService.deposit(
            "user@test.com",
            "PF-ACCOUNT",
            new BigDecimal("50.00")
    );

    assertNotNull(result);

    assertEquals(
            new BigDecimal("150.00"),
            account.getBalance()
    );

    assertEquals(
            new BigDecimal("50.00"),
            result.getAmount()
    );

    assertEquals(
            TransactionStatus.COMPLETED,
            result.getStatus()
    );

    verify(accountRepository).save(account);

    verify(transactionRepository)
            .save(any(Transaction.class));

    verify(notificationService)
            .createNotification(
                    eq(10L),
                    eq("Deposit received"),
                    anyString()
            );

    verify(auditLogService)
            .createLog(
                    eq("DEPOSIT"),
                    eq("user@test.com"),
                    anyString()
            );
}
@Test
void deposit_shouldFail_whenAmountIsInvalid() {

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.deposit(
                    "user@test.com",
                    "PF-ACCOUNT",
                    BigDecimal.ZERO
            )
    );

    assertEquals(
            "Amount must be greater than zero",
            exception.getMessage()
    );

    verify(accountRepository, never())
            .findByAccountNumber(anyString());

    verify(transactionRepository, never())
            .save(any(Transaction.class));
}
@Test
void deposit_shouldFail_whenAccountNotFound() {

    when(accountRepository.findByAccountNumber("PF-ACCOUNT"))
            .thenReturn(Optional.empty());

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.deposit(
                    "user@test.com",
                    "PF-ACCOUNT",
                    new BigDecimal("50.00")
            )
    );

    assertEquals(
            "Account not found",
            exception.getMessage()
    );

    verify(accountRepository, never())
            .save(any(Account.class));

    verify(transactionRepository, never())
            .save(any(Transaction.class));
}
@Test
void deposit_shouldFail_whenUserIsNotAccountOwner() {

    Account account = new Account();
    account.setId(1L);
    account.setAccountNumber("PF-ACCOUNT");
    account.setBalance(new BigDecimal("100.00"));
    account.setStatus(AccountStatus.ACTIVE);

    com.example.demo.entity.User owner =
            new com.example.demo.entity.User();
    owner.setId(10L);
    owner.setEmail("owner@test.com");

    account.setOwner(owner);

    when(accountRepository.findByAccountNumber("PF-ACCOUNT"))
            .thenReturn(Optional.of(account));

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.deposit(
                    "hacker@test.com",
                    "PF-ACCOUNT",
                    new BigDecimal("50.00")
            )
    );

    assertEquals(
            "You are not the owner of this account",
            exception.getMessage()
    );

    verify(accountRepository, never())
            .save(any(Account.class));

    verify(transactionRepository, never())
            .save(any(Transaction.class));
}
@Test
void deposit_shouldFail_whenUserIsNotAccountOwner1() {

    Account account = new Account();
    account.setId(1L);
    account.setAccountNumber("PF-ACCOUNT");
    account.setBalance(new BigDecimal("100.00"));
    account.setStatus(AccountStatus.ACTIVE);

    com.example.demo.entity.User owner =
            new com.example.demo.entity.User();
    owner.setId(10L);
    owner.setEmail("owner@test.com");

    account.setOwner(owner);

    when(accountRepository.findByAccountNumber("PF-ACCOUNT"))
            .thenReturn(Optional.of(account));

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.deposit(
                    "hacker@test.com",
                    "PF-ACCOUNT",
                    new BigDecimal("50.00")
            )
    );

    assertEquals(
            "You are not the owner of this account",
            exception.getMessage()
    );

    verify(accountRepository, never())
            .save(any(Account.class));

    verify(transactionRepository, never())
            .save(any(Transaction.class));
}
@Test
void deposit_shouldFail_whenAccountIsBlocked() {

    Account account = new Account();
    account.setId(1L);
    account.setAccountNumber("PF-ACCOUNT");
    account.setBalance(new BigDecimal("100.00"));
    account.setStatus(AccountStatus.BLOCKED);

    com.example.demo.entity.User user =
            new com.example.demo.entity.User();
    user.setId(10L);
    user.setEmail("user@test.com");

    account.setOwner(user);

    when(accountRepository.findByAccountNumber("PF-ACCOUNT"))
            .thenReturn(Optional.of(account));

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.deposit(
                    "user@test.com",
                    "PF-ACCOUNT",
                    new BigDecimal("50.00")
            )
    );

    assertEquals(
            "Account is not active",
            exception.getMessage()
    );

    verify(accountRepository, never())
            .save(any(Account.class));

    verify(transactionRepository, never())
            .save(any(Transaction.class));
}
@Test
void withdraw_shouldCompleteSuccessfully() {

    Account account = new Account();
    account.setId(1L);
    account.setAccountNumber("PF-ACCOUNT");
    account.setBalance(new BigDecimal("100.00"));
    account.setStatus(AccountStatus.ACTIVE);

    com.example.demo.entity.User user =
            new com.example.demo.entity.User();
    user.setId(10L);
    user.setEmail("user@test.com");

    account.setOwner(user);

    when(accountRepository.findByAccountNumber("PF-ACCOUNT"))
            .thenReturn(Optional.of(account));

    when(transactionRepository.save(any(Transaction.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    Transaction result = transactionService.withdraw(
            "user@test.com",
            "PF-ACCOUNT",
            new BigDecimal("30.00")
    );

    assertNotNull(result);

    assertEquals(
            new BigDecimal("70.00"),
            account.getBalance()
    );

    assertEquals(
            new BigDecimal("30.00"),
            result.getAmount()
    );

    assertEquals(
            TransactionStatus.COMPLETED,
            result.getStatus()
    );

    verify(accountRepository).save(account);

    verify(transactionRepository)
            .save(any(Transaction.class));

    verify(notificationService)
            .createNotification(
                    eq(10L),
                    eq("Withdrawal completed"),
                    anyString()
            );

    verify(auditLogService)
            .createLog(
                    eq("WITHDRAWAL"),
                    eq("user@test.com"),
                    anyString()
            );
}
@Test
void withdraw_shouldFail_whenInsufficientBalance() {

    Account account = new Account();
    account.setId(1L);
    account.setAccountNumber("PF-ACCOUNT");
    account.setBalance(new BigDecimal("20.00"));
    account.setStatus(AccountStatus.ACTIVE);

    com.example.demo.entity.User user =
            new com.example.demo.entity.User();
    user.setId(10L);
    user.setEmail("user@test.com");

    account.setOwner(user);

    when(accountRepository.findByAccountNumber("PF-ACCOUNT"))
            .thenReturn(Optional.of(account));

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.withdraw(
                    "user@test.com",
                    "PF-ACCOUNT",
                    new BigDecimal("50.00")
            )
    );

    assertEquals(
            "Insufficient balance",
            exception.getMessage()
    );

    verify(accountRepository, never())
            .save(any(Account.class));

    verify(transactionRepository, never())
            .save(any(Transaction.class));
}
@Test
void withdraw_shouldFail_whenAmountIsInvalid() {

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.withdraw(
                    "user@test.com",
                    "PF-ACCOUNT",
                    BigDecimal.ZERO
            )
    );

    assertEquals(
            "Amount must be greater than zero",
            exception.getMessage()
    );

    verify(accountRepository, never())
            .findByAccountNumber(anyString());

    verify(transactionRepository, never())
            .save(any(Transaction.class));
}
@Test
void withdraw_shouldFail_whenAccountNotFound() {

    when(accountRepository.findByAccountNumber("PF-ACCOUNT"))
            .thenReturn(Optional.empty());

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.withdraw(
                    "user@test.com",
                    "PF-ACCOUNT",
                    new BigDecimal("50.00")
            )
    );

    assertEquals(
            "Account not found",
            exception.getMessage()
    );

    verify(accountRepository, never())
            .save(any(Account.class));

    verify(transactionRepository, never())
            .save(any(Transaction.class));
}
@Test
void withdraw_shouldFail_whenUserIsNotAccountOwner() {

    Account account = new Account();
    account.setId(1L);
    account.setAccountNumber("PF-ACCOUNT");
    account.setBalance(new BigDecimal("100.00"));
    account.setStatus(AccountStatus.ACTIVE);

    com.example.demo.entity.User owner =
            new com.example.demo.entity.User();
    owner.setId(10L);
    owner.setEmail("owner@test.com");

    account.setOwner(owner);

    when(accountRepository.findByAccountNumber("PF-ACCOUNT"))
            .thenReturn(Optional.of(account));

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.withdraw(
                    "hacker@test.com",
                    "PF-ACCOUNT",
                    new BigDecimal("30.00")
            )
    );

    assertEquals(
            "You are not the owner of this account",
            exception.getMessage()
    );

    verify(accountRepository, never())
            .save(any(Account.class));

    verify(transactionRepository, never())
            .save(any(Transaction.class));
}
@Test
void withdraw_shouldFail_whenAccountIsBlocked() {

    Account account = new Account();
    account.setId(1L);
    account.setAccountNumber("PF-ACCOUNT");
    account.setBalance(new BigDecimal("100.00"));
    account.setStatus(AccountStatus.BLOCKED);

    com.example.demo.entity.User user =
            new com.example.demo.entity.User();
    user.setId(10L);
    user.setEmail("user@test.com");

    account.setOwner(user);

    when(accountRepository.findByAccountNumber("PF-ACCOUNT"))
            .thenReturn(Optional.of(account));

    BusinessException exception = assertThrows(
            BusinessException.class,
            () -> transactionService.withdraw(
                    "user@test.com",
                    "PF-ACCOUNT",
                    new BigDecimal("30.00")
            )
    );

    assertEquals(
            "Account is not active",
            exception.getMessage()
    );

    verify(accountRepository, never())
            .save(any(Account.class));

    verify(transactionRepository, never())
            .save(any(Transaction.class));
}
}