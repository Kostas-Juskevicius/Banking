package com.kostas.banking.dto;

import com.kostas.banking.enums.CurrencyCode;
import com.kostas.banking.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionCreateDTO(
        String referenceNumber,
        UUID debitAccountId,
        UUID creditAccountId,
        BigDecimal amount,
        CurrencyCode currency,
        TransactionType type
) {}

