package com.nikhil.BankingApplication.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    private LocalDateTime timestamp;


    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private BigDecimal balanceAfterTransaction;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_number", nullable = false)
    private Account account;
}
