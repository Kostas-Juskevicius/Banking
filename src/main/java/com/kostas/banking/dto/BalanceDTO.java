package com.kostas.banking.dto;

import com.kostas.banking.enums.CurrencyCode;
import com.kostas.banking.model.Balance;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceDTO(
        UUID id,
        UUID accountId,
        String accountNumber,
        BigDecimal amount,
        CurrencyCode currency
) {
    public static BalanceDTO fromEntity(Balance balance) {
        return new BalanceDTO(
                balance.getId(),
                balance.getAccount().getId(),
                balance.getAccount().getAccountNumber(),
                balance.getAmount(),
                balance.getCurrency()
        );
    }
}

