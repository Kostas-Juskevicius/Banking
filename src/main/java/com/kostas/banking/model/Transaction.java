package com.kostas.banking.model;

import com.kostas.banking.enums.CurrencyCode;
import com.kostas.banking.enums.TransactionStatus;
import com.kostas.banking.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String referenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account debitAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account creditAccount;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyCode currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime postedAt;
}