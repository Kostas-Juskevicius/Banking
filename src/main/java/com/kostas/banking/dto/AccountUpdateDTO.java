package com.kostas.banking.dto;

import com.kostas.banking.enums.AccountStatus;
import com.kostas.banking.enums.AccountType;
import com.kostas.banking.model.Account;

public record AccountUpdateDTO(
        AccountType type,
        AccountStatus status
) {
    public static AccountUpdateDTO fromEntity(Account account) {
        return new AccountUpdateDTO(
                account.getType(),
                account.getStatus()
        );
    }
}

