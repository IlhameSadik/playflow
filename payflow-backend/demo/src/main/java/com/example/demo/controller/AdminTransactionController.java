
package com.example.demo.controller;

import com.example.demo.dto.TransactionResponse;
import com.example.demo.entity.Transaction;
import com.example.demo.service.TransactionService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/transactions")
public class AdminTransactionController {

    private final TransactionService transactionService;

    public AdminTransactionController(
            TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {

        List<TransactionResponse> response =
                transactionService.getAllTransactions()
                        .stream()
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
    
@GetMapping("/filter")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<TransactionResponse>> filterTransactions(
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String search) {

    List<TransactionResponse> response =
            transactionService.filterTransactions(type, status, search)
                    .stream()
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

}
