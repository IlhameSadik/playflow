package com.example.demo.controller;

import com.example.demo.dto.DepositRequest;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.dto.TransferRequest;
import com.example.demo.dto.WithdrawalRequest;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.TransactionService;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(
            TransactionService transactionService,
            UserService userService) {

        this.transactionService = transactionService;
        this.userService = userService;
    }

    // =========================
    // DEPOSIT
    // =========================

    @PostMapping("/deposit/{accountNumber}")
    public ResponseEntity<TransactionResponse> deposit(
            @PathVariable String accountNumber,
            @Valid @RequestBody DepositRequest request,
            Authentication authentication) {

        Transaction transaction =
                transactionService.deposit(
                        authentication.getName(),
                        accountNumber,
                        request.getAmount()
                );

        TransactionResponse response = new TransactionResponse(
                transaction.getId(),
                transaction.getSourceAccount().getAccountNumber(),
                transaction.getDestinationAccount().getAccountNumber(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    // =========================
    // TRANSFER
    // =========================

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {

        Transaction transaction =
                transactionService.transfer(
                        authentication.getName(),
                        request.getSourceAccountNumber(),
                        request.getDestinationAccountNumber(),
                        request.getAmount(),
                        request.getDescription(),
                        request.getIdempotencyKey()
                );

        TransactionResponse response = new TransactionResponse(
                transaction.getId(),
                transaction.getSourceAccount().getAccountNumber(),
                transaction.getDestinationAccount().getAccountNumber(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    // =========================
    // MY TRANSACTIONS
    // =========================

    @GetMapping("/my-transactions")
    public ResponseEntity<List<TransactionResponse>> getMyTransactions(
            Authentication authentication) {

        User user = userService
                .getUserByEmail(authentication.getName())
                .orElseThrow(() ->
                        new BusinessException("User not found"));

        List<Transaction> transactions =
                transactionService.getUserTransactions(user.getId());

        List<TransactionResponse> response =
                transactions.stream()
                        .map(transaction -> new TransactionResponse(
                                transaction.getId(),
                                transaction.getSourceAccount().getAccountNumber(),
                                transaction.getDestinationAccount().getAccountNumber(),
                                transaction.getAmount(),
                                transaction.getDescription(),
                                transaction.getType(),
                                transaction.getStatus(),
                                transaction.getCreatedAt()
                        ))
                        .toList();

        return ResponseEntity.ok(response);
    }

    // =========================
    // GET TRANSACTION BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id,
            Authentication authentication) {

        Transaction transaction =
                transactionService.getTransactionById(id);

        boolean isSourceOwner =
                transaction.getSourceAccount()
                        .getOwner()
                        .getEmail()
                        .equals(authentication.getName());

        boolean isDestinationOwner =
                transaction.getDestinationAccount()
                        .getOwner()
                        .getEmail()
                        .equals(authentication.getName());

        if (!isSourceOwner && !isDestinationOwner) {
            return ResponseEntity.status(403).build();
        }

        TransactionResponse response = new TransactionResponse(
                transaction.getId(),
                transaction.getSourceAccount().getAccountNumber(),
                transaction.getDestinationAccount().getAccountNumber(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    // =========================
    // WITHDRAWAL
    // =========================

    @PostMapping("/withdraw/{accountNumber}")
    public ResponseEntity<TransactionResponse> withdraw(
            @PathVariable String accountNumber,
            @Valid @RequestBody WithdrawalRequest request,
            Authentication authentication) {

        Transaction transaction =
                transactionService.withdraw(
                        authentication.getName(),
                        accountNumber,
                        request.getAmount()
                );

        TransactionResponse response = new TransactionResponse(
                transaction.getId(),
                transaction.getSourceAccount().getAccountNumber(),
                transaction.getDestinationAccount().getAccountNumber(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }
}