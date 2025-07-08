package com.kostas.banking.model;

import com.kostas.banking.enums.CurrencyCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "balances", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"account_id", "currency"})
})
public class Balance {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Column(precision = 19, scale = 4) // ISO 20022 standard
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private CurrencyCode currency;
}
