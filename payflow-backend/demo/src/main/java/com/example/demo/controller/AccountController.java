package com.example.demo.controller;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.exception.BusinessException;
import com.example.demo.entity.Account;
import com.example.demo.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.example.demo.dto.AccountResponse;
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    private final UserService userService;

public AccountController(
        AccountService accountService,
        UserService userService) {

    this.accountService = accountService;
    this.userService = userService;
}

   @PostMapping("/user/{userId}")
public ResponseEntity<AccountResponse> createAccount(
        @PathVariable Long userId,
        Authentication authentication) {

    if (!accountService.isUserOwner(userId, authentication.getName())) {
        return ResponseEntity.status(403).build();
    }

    Account account = accountService.createAccount(userId);

    return ResponseEntity.ok(toResponse(account));
}
 @GetMapping("/user/{userId}")
public ResponseEntity<AccountResponse> getAccountByUser(
        @PathVariable Long userId,
        Authentication authentication) {

    Account account = accountService.getAccountByUserId(userId);

    if (!account.getOwner().getEmail()
            .equals(authentication.getName())) {

        return ResponseEntity.status(403).build();
    }

    return ResponseEntity.ok(toResponse(account));
}
@GetMapping("/me")
public ResponseEntity<AccountResponse> getMyAccount(
        Authentication authentication) {

    User user = userService
            .getUserByEmail(authentication.getName())
            .orElseThrow(() ->
                    new BusinessException("User not found"));

    Account account =
            accountService.getAccountByUserId(user.getId());

    return ResponseEntity.ok(toResponse(account));
}
private AccountResponse toResponse(Account account) {
    return new AccountResponse(
            account.getId(),
            account.getAccountNumber(),
            account.getBalance(),
            account.getCurrency(),
            account.getStatus().name(),
            account.getCreatedAt()
    );
}
}