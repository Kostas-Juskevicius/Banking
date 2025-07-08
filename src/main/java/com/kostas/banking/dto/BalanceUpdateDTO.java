package com.kostas.banking.dto;

import com.kostas.banking.model.Balance;

import java.math.BigDecimal;

public record BalanceUpdateDTO(
        BigDecimal amount
) {
    public static BalanceUpdateDTO fromEntity(Balance balance) {
        return new BalanceUpdateDTO(
                balance.getAmount()
        );
    }
}

