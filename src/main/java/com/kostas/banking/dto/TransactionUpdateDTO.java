package com.kostas.banking.dto;

import com.kostas.banking.enums.TransactionStatus;
import com.kostas.banking.model.Transaction;

import java.time.LocalDateTime;

public record TransactionUpdateDTO(
        TransactionStatus status,
        LocalDateTime postedAt
) {
    public static TransactionUpdateDTO fromEntity(Transaction transaction) {
        return new TransactionUpdateDTO(
                transaction.getStatus(),
                transaction.getPostedAt()
        );
    }
}

