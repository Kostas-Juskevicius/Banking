package com.kostas.banking.dto;

import com.kostas.banking.enums.AccountStatus;
import com.kostas.banking.enums.AccountType;
import com.kostas.banking.model.Account;

import java.util.UUID;

public record AccountDTO(
        UUID id,
        String accountNumber,
        AccountType type,
        AccountStatus status,
        UUID ownerId,
        String ownerFullName
) {
    public static AccountDTO fromEntity(Account account) {
        return new AccountDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getType(),
                account.getStatus(),
                account.getOwner().getId(),
                account.getOwner().getFullName()
        );
    }
}

