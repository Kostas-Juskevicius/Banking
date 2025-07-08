package com.kostas.banking.model;

import com.kostas.banking.enums.AccountType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(indexes = {
    @Index(name = "idx_account_number", columnList = "accountNumber")
})
public class Account {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer owner;

    @OneToMany(mappedBy = "account")
    private List<Balance> balances;
}
