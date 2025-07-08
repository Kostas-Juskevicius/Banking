package com.kostas.banking.dto;

import com.kostas.banking.enums.CurrencyCode;
import com.kostas.banking.enums.TransactionStatus;
import com.kostas.banking.enums.TransactionType;
import com.kostas.banking.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionDTO(
        UUID id,
        String referenceNumber,
        UUID debitAccountId,
        String debitAccountNumber,
        UUID creditAccountId,
        String creditAccountNumber,
        BigDecimal amount,
        CurrencyCode currency,
        TransactionType type,
        TransactionStatus status,
        LocalDateTime createdAt,
        LocalDateTime postedAt
) {
    public static TransactionDTO fromEntity(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getReferenceNumber(),
                transaction.getDebitAccount() != null ? transaction.getDebitAccount().getId() : null,
                transaction.getDebitAccount() != null ? transaction.getDebitAccount().getAccountNumber() : null,
                transaction.getCreditAccount() != null ? transaction.getCreditAccount().getId() : null,
                transaction.getCreditAccount() != null ? transaction.getCreditAccount().getAccountNumber() : null,
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getCreatedAt(),
                transaction.getPostedAt()
        );
    }
}

