package com.kostas.banking.dto;

import com.kostas.banking.enums.AccountType;
import com.kostas.banking.model.Account;

public record AccountUpdateDTO(
        AccountType type
) {
    public static AccountUpdateDTO fromEntity(Account account) {
        return new AccountUpdateDTO(
                account.getType()
        );
    }
}

