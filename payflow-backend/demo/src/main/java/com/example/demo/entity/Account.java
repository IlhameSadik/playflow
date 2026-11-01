package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private String currency;

@Enumerated(EnumType.STRING)
@Column(nullable = false)
private AccountStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    @OneToOne
@JoinColumn(name = "user_id", nullable = false, unique = true)
private User owner;

}