package com.example.demo.controller;

import com.example.demo.entity.Account;
import com.example.demo.entity.TransactionStatus;
import com.example.demo.entity.TransactionType;
import com.example.demo.service.TransactionService;
import com.example.demo.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        TransactionController controller =
                new TransactionController(
                        transactionService,
                        userService
                );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

@Test
void transfer_shouldReturn400_whenRequestBodyIsMissing() throws Exception {

    mockMvc.perform(
            post("/api/transactions/transfer")
    )
    .andExpect(status().isBadRequest());
}
@Test
void transfer_shouldReturn200_whenRequestIsValid() throws Exception {

    com.example.demo.entity.User sourceUser =
            new com.example.demo.entity.User();
    sourceUser.setId(10L);
    sourceUser.setEmail("user@test.com");

    com.example.demo.entity.User destinationUser =
            new com.example.demo.entity.User();
    destinationUser.setId(20L);
    destinationUser.setEmail("dest@test.com");

    com.example.demo.entity.Account source =
            new com.example.demo.entity.Account();
    source.setId(1L);
    source.setAccountNumber("PF-SOURCE");
    source.setOwner(sourceUser);

    com.example.demo.entity.Account destination =
            new com.example.demo.entity.Account();
    destination.setId(2L);
    destination.setAccountNumber("PF-DEST");
    destination.setOwner(destinationUser);

    com.example.demo.entity.Transaction transaction =
            new com.example.demo.entity.Transaction();

    transaction.setId(1L);
    transaction.setSourceAccount(source);
    transaction.setDestinationAccount(destination);
    transaction.setAmount(new java.math.BigDecimal("30.00"));
    transaction.setDescription("Test transfer");
    transaction.setType(
            com.example.demo.entity.TransactionType.TRANSFER
    );
    transaction.setStatus(
            com.example.demo.entity.TransactionStatus.COMPLETED
    );
    transaction.setCreatedAt(
            java.time.LocalDateTime.now()
    );

    org.mockito.Mockito.when(
            transactionService.transfer(
                    org.mockito.ArgumentMatchers.eq("user@test.com"),
                    org.mockito.ArgumentMatchers.eq("PF-SOURCE"),
                    org.mockito.ArgumentMatchers.eq("PF-DEST"),
                    org.mockito.ArgumentMatchers.any(),
                    org.mockito.ArgumentMatchers.eq("Test transfer"),
                    org.mockito.ArgumentMatchers.eq("TEST-010")
            )
    ).thenReturn(transaction);

mockMvc.perform(
        post("/api/transactions/transfer")
                .principal(
                        new UsernamePasswordAuthenticationToken(
                                "user@test.com",
                                null
                        )
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "sourceAccountNumber": "PF-SOURCE",
                        "destinationAccountNumber": "PF-DEST",
                        "amount": 30.00,
                        "description": "Test transfer",
                        "idempotencyKey": "TEST-010"
                    }
                """)
)
.andExpect(status().isOk());
}
@Test
void deposit_shouldReturn400_whenRequestBodyIsMissing() throws Exception {

    mockMvc.perform(
            post("/api/transactions/deposit/PF-SOURCE")
    )
    .andExpect(status().isBadRequest());
}
@Test
void deposit_shouldReturn200_whenRequestIsValid() throws Exception {

    User user = new User();
    user.setId(10L);
    user.setEmail("user@test.com");

    Account account = new Account();
    account.setId(1L);
    account.setAccountNumber("PF-SOURCE");
    account.setOwner(user);

    Transaction transaction = new Transaction();
    transaction.setId(2L);
    transaction.setSourceAccount(account);
    transaction.setDestinationAccount(account);
    transaction.setAmount(new BigDecimal("100.00"));
    transaction.setDescription("Deposit");
    transaction.setType(TransactionType.DEPOSIT);
    transaction.setStatus(TransactionStatus.COMPLETED);
    transaction.setCreatedAt(LocalDateTime.now());

    when(transactionService.deposit(
            eq("user@test.com"),
            eq("PF-SOURCE"),
            any(BigDecimal.class)
    )).thenReturn(transaction);

    mockMvc.perform(
            post("/api/transactions/deposit/PF-SOURCE")
                    .principal(new UsernamePasswordAuthenticationToken(
                            "user@test.com", null
                    ))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "amount": 100.00
                        }
                    """)
    )
    .andExpect(status().isOk());
}
@Test
void withdraw_shouldReturn200_whenRequestIsValid() throws Exception {

    User user = new User();
    user.setId(10L);
    user.setEmail("user@test.com");

    Account account = new Account();
    account.setId(1L);
    account.setAccountNumber("PF-SOURCE");
    account.setOwner(user);

    Transaction transaction = new Transaction();
    transaction.setId(3L);
    transaction.setSourceAccount(account);
    transaction.setDestinationAccount(account);
    transaction.setAmount(new BigDecimal("50.00"));
    transaction.setDescription("Withdrawal");
    transaction.setType(TransactionType.WITHDRAWAL);
    transaction.setStatus(TransactionStatus.COMPLETED);
    transaction.setCreatedAt(LocalDateTime.now());

    when(transactionService.withdraw(
            eq("user@test.com"),
            eq("PF-SOURCE"),
            any(BigDecimal.class)
    )).thenReturn(transaction);

    mockMvc.perform(
            post("/api/transactions/withdraw/PF-SOURCE")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "amount": 50.00
                        }
                    """)
    )
    .andExpect(status().isOk());
}
@Test
void deposit_shouldReturn400_whenAmountIsInvalid() throws Exception {

    mockMvc.perform(
            post("/api/transactions/deposit/PF-SOURCE")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "amount": -100
                        }
                    """)
    )
    .andExpect(status().isBadRequest());
}
@Test
void transfer_shouldReturn400_whenAmountIsInvalid() throws Exception {

    mockMvc.perform(
            post("/api/transactions/transfer")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "sourceAccountNumber": "PF-SOURCE",
                            "destinationAccountNumber": "PF-DEST",
                            "amount": -50,
                            "description": "Test transfer",
                            "idempotencyKey": "TEST-INVALID-001"
                        }
                    """)
    )
    .andExpect(status().isBadRequest());
}
@Test
void withdraw_shouldReturn400_whenAmountIsInvalid() throws Exception {

    mockMvc.perform(
            post("/api/transactions/withdraw/PF-SOURCE")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "amount": -50
                        }
                    """)
    )
    .andExpect(status().isBadRequest());
}
@Test
void getMyTransactions_shouldReturn200_whenUserIsAuthenticated() throws Exception {

    User user = new User();
    user.setId(10L);
    user.setEmail("user@test.com");

    when(userService.getUserByEmail("user@test.com"))
            .thenReturn(java.util.Optional.of(user));

    when(transactionService.getUserTransactions(10L))
            .thenReturn(java.util.List.of());

    mockMvc.perform(
            get("/api/transactions/my-transactions")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
    )
    .andExpect(status().isOk());
}

@Test
void getTransactionById_shouldReturn200_whenUserIsOwner() throws Exception {

    User user = new User();
    user.setId(10L);
    user.setEmail("user@test.com");

    Account account = new Account();
    account.setId(1L);
    account.setAccountNumber("PF-SOURCE");
    account.setOwner(user);

    Transaction transaction = new Transaction();
    transaction.setId(10L);
    transaction.setSourceAccount(account);
    transaction.setDestinationAccount(account);
    transaction.setAmount(new BigDecimal("50.00"));
    transaction.setDescription("Test transaction");
    transaction.setType(TransactionType.TRANSFER);
    transaction.setStatus(TransactionStatus.COMPLETED);
    transaction.setCreatedAt(LocalDateTime.now());

    when(transactionService.getTransactionById(10L))
            .thenReturn(transaction);

    mockMvc.perform(
            get("/api/transactions/10")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
    )
    .andExpect(status().isOk());
}
@Test
void getTransactionById_shouldReturn403_whenUserIsNotOwner() throws Exception {

    User owner = new User();
    owner.setId(10L);
    owner.setEmail("owner@test.com");

    Account source = new Account();
    source.setId(1L);
    source.setAccountNumber("PF-SOURCE");
    source.setOwner(owner);

    Account destination = new Account();
    destination.setId(2L);
    destination.setAccountNumber("PF-DEST");
    destination.setOwner(owner);

    Transaction transaction = new Transaction();
    transaction.setId(20L);
    transaction.setSourceAccount(source);
    transaction.setDestinationAccount(destination);
    transaction.setAmount(new BigDecimal("50.00"));
    transaction.setDescription("Private transaction");
    transaction.setType(TransactionType.TRANSFER);
    transaction.setStatus(TransactionStatus.COMPLETED);
    transaction.setCreatedAt(LocalDateTime.now());

    when(transactionService.getTransactionById(20L))
            .thenReturn(transaction);

    mockMvc.perform(
            get("/api/transactions/20")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "other@test.com",
                                    null
                            )
                    )
    )
    .andExpect(status().isForbidden());
}
@Test
void getMyTransactions_shouldReturnTransactions() throws Exception {

    User user = new User();
    user.setId(10L);
    user.setEmail("user@test.com");

    Account account = new Account();
    account.setId(1L);
    account.setAccountNumber("PF-SOURCE");
    account.setOwner(user);

    Transaction transaction = new Transaction();
    transaction.setId(30L);
    transaction.setSourceAccount(account);
    transaction.setDestinationAccount(account);
    transaction.setAmount(new BigDecimal("100.00"));
    transaction.setDescription("Test deposit");
    transaction.setType(TransactionType.DEPOSIT);
    transaction.setStatus(TransactionStatus.COMPLETED);
    transaction.setCreatedAt(LocalDateTime.now());

    when(userService.getUserByEmail("user@test.com"))
            .thenReturn(java.util.Optional.of(user));

    when(transactionService.getUserTransactions(10L))
            .thenReturn(java.util.List.of(transaction));

    mockMvc.perform(
            get("/api/transactions/my-transactions")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
    )
    .andExpect(status().isOk());
}
@Test
void deposit_shouldReturn400_whenAmountIsZero() throws Exception {

    mockMvc.perform(
            post("/api/transactions/deposit/PF-SOURCE")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "amount": 0
                        }
                    """)
    )
    .andExpect(status().isBadRequest());
}
@Test
void withdraw_shouldReturn400_whenAmountIsZero() throws Exception {

    mockMvc.perform(
            post("/api/transactions/withdraw/PF-SOURCE")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "amount": 0
                        }
                    """)
    )
    .andExpect(status().isBadRequest());
}
@Test
void transfer_shouldReturn400_whenAmountIsZero() throws Exception {

    mockMvc.perform(
            post("/api/transactions/transfer")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "sourceAccountNumber": "PF-SOURCE",
                            "destinationAccountNumber": "PF-DEST",
                            "amount": 0,
                            "description": "Test transfer",
                            "idempotencyKey": "TEST-ZERO-001"
                        }
                    """)
    )
    .andExpect(status().isBadRequest());
}
@Test
void getTransactionById_shouldReturn200_whenUserIsDestinationOwner() throws Exception {

    User sourceUser = new User();
    sourceUser.setId(10L);
    sourceUser.setEmail("source@test.com");

    User destinationUser = new User();
    destinationUser.setId(20L);
    destinationUser.setEmail("destination@test.com");

    Account source = new Account();
    source.setId(1L);
    source.setAccountNumber("PF-SOURCE");
    source.setOwner(sourceUser);

    Account destination = new Account();
    destination.setId(2L);
    destination.setAccountNumber("PF-DEST");
    destination.setOwner(destinationUser);

    Transaction transaction = new Transaction();
    transaction.setId(40L);
    transaction.setSourceAccount(source);
    transaction.setDestinationAccount(destination);
    transaction.setAmount(new BigDecimal("100.00"));
    transaction.setDescription("Transfer received");
    transaction.setType(TransactionType.TRANSFER);
    transaction.setStatus(TransactionStatus.COMPLETED);
    transaction.setCreatedAt(LocalDateTime.now());

    when(transactionService.getTransactionById(40L))
            .thenReturn(transaction);

    mockMvc.perform(
            get("/api/transactions/40")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "destination@test.com",
                                    null
                            )
                    )
    )
    .andExpect(status().isOk());
}
@Test
void deposit_shouldReturn400_whenAmountIsMissing() throws Exception {

    mockMvc.perform(
            post("/api/transactions/deposit/PF-SOURCE")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {}
                    """)
    )
    .andExpect(status().isBadRequest());
}
@Test
void withdraw_shouldReturn400_whenAmountIsMissing() throws Exception {

    mockMvc.perform(
            post("/api/transactions/withdraw/PF-SOURCE")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {}
                    """)
    )
    .andExpect(status().isBadRequest());
}
@Test
void transfer_shouldReturn400_whenAmountIsMissing() throws Exception {

    mockMvc.perform(
            post("/api/transactions/transfer")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "sourceAccountNumber": "PF-SOURCE",
                            "destinationAccountNumber": "PF-DEST",
                            "description": "Test transfer",
                            "idempotencyKey": "TEST-MISSING-AMOUNT"
                        }
                    """)
    )
    .andExpect(status().isBadRequest());
}
@Test
void transfer_shouldReturn400_whenSourceAccountIsMissing() throws Exception {

    mockMvc.perform(
            post("/api/transactions/transfer")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "destinationAccountNumber": "PF-DEST",
                            "amount": 50.00,
                            "description": "Test transfer",
                            "idempotencyKey": "TEST-MISSING-SOURCE"
                        }
                    """)
    )
    .andExpect(status().isBadRequest());
}
@Test
void transfer_shouldReturn400_whenDestinationAccountIsMissing() throws Exception {

    mockMvc.perform(
            post("/api/transactions/transfer")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "sourceAccountNumber": "PF-SOURCE",
                            "amount": 50.00,
                            "description": "Test transfer",
                            "idempotencyKey": "TEST-MISSING-DESTINATION"
                        }
                    """)
    )
    .andExpect(status().isBadRequest());
}
@Test
void transfer_shouldReturnCorrectResponse() throws Exception {

    User sourceUser = new User();
    sourceUser.setEmail("user@test.com");

    User destinationUser = new User();
    destinationUser.setEmail("dest@test.com");

    Account sourceAccount = new Account();
    sourceAccount.setAccountNumber("PF-SOURCE");
    sourceAccount.setOwner(sourceUser);

    Account destinationAccount = new Account();
    destinationAccount.setAccountNumber("PF-DEST");
    destinationAccount.setOwner(destinationUser);

    Transaction transaction = new Transaction();
    transaction.setId(25L);
    transaction.setSourceAccount(sourceAccount);
    transaction.setDestinationAccount(destinationAccount);
    transaction.setAmount(new BigDecimal("50.00"));
    transaction.setDescription("Test transfer");
    transaction.setType(TransactionType.TRANSFER);
    transaction.setStatus(TransactionStatus.COMPLETED);
    transaction.setCreatedAt(LocalDateTime.now());

    when(transactionService.transfer(
            eq("user@test.com"),
            eq("PF-SOURCE"),
            eq("PF-DEST"),
            eq(new BigDecimal("50.00")),
            eq("Test transfer"),
            eq("TEST-RESPONSE")
    )).thenReturn(transaction);

    mockMvc.perform(
            post("/api/transactions/transfer")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "sourceAccountNumber": "PF-SOURCE",
                            "destinationAccountNumber": "PF-DEST",
                            "amount": 50.00,
                            "description": "Test transfer",
                            "idempotencyKey": "TEST-RESPONSE"
                        }
                    """)
    )
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.id").value(25))
    .andExpect(jsonPath("$.sourceAccountNumber").value("PF-SOURCE"))
    .andExpect(jsonPath("$.destinationAccountNumber").value("PF-DEST"))
    .andExpect(jsonPath("$.amount").value(50.00))
    .andExpect(jsonPath("$.description").value("Test transfer"))
    .andExpect(jsonPath("$.type").value("TRANSFER"))
    .andExpect(jsonPath("$.status").value("COMPLETED"));
}
@Test
void deposit_shouldReturnCorrectResponse() throws Exception {

    User user = new User();
    user.setEmail("user@test.com");

    Account account = new Account();
    account.setAccountNumber("PF-SOURCE");
    account.setOwner(user);

    Transaction transaction = new Transaction();
    transaction.setId(26L);
    transaction.setSourceAccount(account);
    transaction.setDestinationAccount(account);
    transaction.setAmount(new BigDecimal("100.00"));
    transaction.setDescription("Test deposit");
    transaction.setType(TransactionType.DEPOSIT);
    transaction.setStatus(TransactionStatus.COMPLETED);
    transaction.setCreatedAt(LocalDateTime.now());

    when(transactionService.deposit(
            eq("user@test.com"),
            eq("PF-SOURCE"),
            eq(new BigDecimal("100.00"))
    )).thenReturn(transaction);

    mockMvc.perform(
            post("/api/transactions/deposit/PF-SOURCE")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "amount": 100.00
                        }
                    """)
    )
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.id").value(26))
    .andExpect(jsonPath("$.sourceAccountNumber").value("PF-SOURCE"))
    .andExpect(jsonPath("$.destinationAccountNumber").value("PF-SOURCE"))
    .andExpect(jsonPath("$.amount").value(100.00))
    .andExpect(jsonPath("$.description").value("Test deposit"))
    .andExpect(jsonPath("$.type").value("DEPOSIT"))
    .andExpect(jsonPath("$.status").value("COMPLETED"));
}
@Test
void withdraw_shouldReturnCorrectResponse() throws Exception {

    User user = new User();
    user.setEmail("user@test.com");

    Account account = new Account();
    account.setAccountNumber("PF-SOURCE");
    account.setOwner(user);

    Transaction transaction = new Transaction();
    transaction.setId(27L);
    transaction.setSourceAccount(account);
    transaction.setDestinationAccount(account);
    transaction.setAmount(new BigDecimal("50.00"));
    transaction.setDescription("Test withdrawal");
    transaction.setType(TransactionType.WITHDRAWAL);
    transaction.setStatus(TransactionStatus.COMPLETED);
    transaction.setCreatedAt(LocalDateTime.now());

    when(transactionService.withdraw(
            eq("user@test.com"),
            eq("PF-SOURCE"),
            eq(new BigDecimal("50.00"))
    )).thenReturn(transaction);

    mockMvc.perform(
            post("/api/transactions/withdraw/PF-SOURCE")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "amount": 50.00
                        }
                    """)
    )
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.id").value(27))
    .andExpect(jsonPath("$.sourceAccountNumber").value("PF-SOURCE"))
    .andExpect(jsonPath("$.destinationAccountNumber").value("PF-SOURCE"))
    .andExpect(jsonPath("$.amount").value(50.00))
    .andExpect(jsonPath("$.description").value("Test withdrawal"))
    .andExpect(jsonPath("$.type").value("WITHDRAWAL"))
    .andExpect(jsonPath("$.status").value("COMPLETED"));
}
@Test
void getMyTransactions_shouldReturnMultipleTransactions() throws Exception {

    User user = new User();
    user.setId(10L);
    user.setEmail("user@test.com");

    Account account = new Account();
    account.setAccountNumber("PF-SOURCE");
    account.setOwner(user);

    Transaction deposit = new Transaction();
    deposit.setId(30L);
    deposit.setSourceAccount(account);
    deposit.setDestinationAccount(account);
    deposit.setAmount(new BigDecimal("100.00"));
    deposit.setDescription("Deposit");
    deposit.setType(TransactionType.DEPOSIT);
    deposit.setStatus(TransactionStatus.COMPLETED);
    deposit.setCreatedAt(LocalDateTime.now());

    Transaction withdrawal = new Transaction();
    withdrawal.setId(31L);
    withdrawal.setSourceAccount(account);
    withdrawal.setDestinationAccount(account);
    withdrawal.setAmount(new BigDecimal("40.00"));
    withdrawal.setDescription("Withdrawal");
    withdrawal.setType(TransactionType.WITHDRAWAL);
    withdrawal.setStatus(TransactionStatus.COMPLETED);
    withdrawal.setCreatedAt(LocalDateTime.now());

    when(userService.getUserByEmail("user@test.com"))
            .thenReturn(java.util.Optional.of(user));

    when(transactionService.getUserTransactions(10L))
            .thenReturn(java.util.List.of(deposit, withdrawal));

    mockMvc.perform(
            get("/api/transactions/my-transactions")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
    )
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.length()").value(2))
    .andExpect(jsonPath("$[0].id").value(30))
    .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
    .andExpect(jsonPath("$[1].id").value(31))
    .andExpect(jsonPath("$[1].type").value("WITHDRAWAL"));
}
@Test
void getTransactionById_shouldReturnCorrectResponse() throws Exception {

    User user = new User();
    user.setEmail("user@test.com");

    Account account = new Account();
    account.setAccountNumber("PF-SOURCE");
    account.setOwner(user);

    Transaction transaction = new Transaction();
    transaction.setId(40L);
    transaction.setSourceAccount(account);
    transaction.setDestinationAccount(account);
    transaction.setAmount(new BigDecimal("75.00"));
    transaction.setDescription("Test transaction");
    transaction.setType(TransactionType.DEPOSIT);
    transaction.setStatus(TransactionStatus.COMPLETED);
    transaction.setCreatedAt(LocalDateTime.now());

    when(transactionService.getTransactionById(40L))
            .thenReturn(transaction);

    mockMvc.perform(
            get("/api/transactions/40")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
    )
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.id").value(40))
    .andExpect(jsonPath("$.sourceAccountNumber").value("PF-SOURCE"))
    .andExpect(jsonPath("$.destinationAccountNumber").value("PF-SOURCE"))
    .andExpect(jsonPath("$.amount").value(75.00))
    .andExpect(jsonPath("$.description").value("Test transaction"))
    .andExpect(jsonPath("$.type").value("DEPOSIT"))
    .andExpect(jsonPath("$.status").value("COMPLETED"));
}
@Test
void getMyTransactions_shouldReturnEmptyList_whenNoTransactions() throws Exception {

    User user = new User();
    user.setId(15L);
    user.setEmail("user@test.com");

    when(userService.getUserByEmail("user@test.com"))
            .thenReturn(java.util.Optional.of(user));

    when(transactionService.getUserTransactions(15L))
            .thenReturn(java.util.List.of());

    mockMvc.perform(
            get("/api/transactions/my-transactions")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
    )
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.length()").value(0));
}
@Test
void transfer_shouldReturn200_whenAmountIsMinimalValidValue() throws Exception {

    User sourceUser = new User();
    sourceUser.setEmail("user@test.com");

    User destinationUser = new User();
    destinationUser.setEmail("dest@test.com");

    Account sourceAccount = new Account();
    sourceAccount.setAccountNumber("PF-SOURCE");
    sourceAccount.setOwner(sourceUser);

    Account destinationAccount = new Account();
    destinationAccount.setAccountNumber("PF-DEST");
    destinationAccount.setOwner(destinationUser);

    Transaction transaction = new Transaction();
    transaction.setId(60L);
    transaction.setSourceAccount(sourceAccount);
    transaction.setDestinationAccount(destinationAccount);
    transaction.setAmount(new BigDecimal("0.01"));
    transaction.setDescription("Minimal transfer");
    transaction.setType(TransactionType.TRANSFER);
    transaction.setStatus(TransactionStatus.COMPLETED);
    transaction.setCreatedAt(LocalDateTime.now());

    when(transactionService.transfer(
            eq("user@test.com"),
            eq("PF-SOURCE"),
            eq("PF-DEST"),
            eq(new BigDecimal("0.01")),
            eq("Minimal transfer"),
            eq("TEST-MINIMAL")
    )).thenReturn(transaction);

    mockMvc.perform(
            post("/api/transactions/transfer")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "sourceAccountNumber": "PF-SOURCE",
                            "destinationAccountNumber": "PF-DEST",
                            "amount": 0.01,
                            "description": "Minimal transfer",
                            "idempotencyKey": "TEST-MINIMAL"
                        }
                    """)
    )
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.amount").value(0.01))
    .andExpect(jsonPath("$.type").value("TRANSFER"))
    .andExpect(jsonPath("$.status").value("COMPLETED"));
}
@Test
void deposit_shouldReturn200_whenAmountIsMinimalValidValue() throws Exception {

    User user = new User();
    user.setEmail("user@test.com");

    Account account = new Account();
    account.setAccountNumber("PF-SOURCE");
    account.setOwner(user);

    Transaction transaction = new Transaction();
    transaction.setId(61L);
    transaction.setSourceAccount(account);
    transaction.setDestinationAccount(account);
    transaction.setAmount(new BigDecimal("0.01"));
    transaction.setDescription("Minimal deposit");
    transaction.setType(TransactionType.DEPOSIT);
    transaction.setStatus(TransactionStatus.COMPLETED);
    transaction.setCreatedAt(LocalDateTime.now());

    when(transactionService.deposit(
            eq("user@test.com"),
            eq("PF-SOURCE"),
            eq(new BigDecimal("0.01"))
    )).thenReturn(transaction);

    mockMvc.perform(
            post("/api/transactions/deposit/PF-SOURCE")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "amount": 0.01
                        }
                    """)
    )
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.amount").value(0.01))
    .andExpect(jsonPath("$.type").value("DEPOSIT"))
    .andExpect(jsonPath("$.status").value("COMPLETED"));
}
@Test
void withdraw_shouldReturn200_whenAmountIsMinimalValidValue() throws Exception {

    User user = new User();
    user.setEmail("user@test.com");

    Account account = new Account();
    account.setAccountNumber("PF-SOURCE");
    account.setOwner(user);

    Transaction transaction = new Transaction();
    transaction.setId(62L);
    transaction.setSourceAccount(account);
    transaction.setDestinationAccount(account);
    transaction.setAmount(new BigDecimal("0.01"));
    transaction.setDescription("Minimal withdrawal");
    transaction.setType(TransactionType.WITHDRAWAL);
    transaction.setStatus(TransactionStatus.COMPLETED);
    transaction.setCreatedAt(LocalDateTime.now());

    when(transactionService.withdraw(
            eq("user@test.com"),
            eq("PF-SOURCE"),
            eq(new BigDecimal("0.01"))
    )).thenReturn(transaction);

    mockMvc.perform(
            post("/api/transactions/withdraw/PF-SOURCE")
                    .principal(
                            new UsernamePasswordAuthenticationToken(
                                    "user@test.com",
                                    null
                            )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "amount": 0.01
                        }
                    """)
    )
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.amount").value(0.01))
    .andExpect(jsonPath("$.type").value("WITHDRAWAL"))
    .andExpect(jsonPath("$.status").value("COMPLETED"));
}
}