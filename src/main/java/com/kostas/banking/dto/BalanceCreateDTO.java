package com.kostas.banking.dto;

import com.kostas.banking.enums.CurrencyCode;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceCreateDTO(
        UUID accountId,
        BigDecimal amount,
        CurrencyCode currency
) {}

