package com.kostas.banking.exception;

import java.util.UUID;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(UUID id) {
        super("Transaction not found with ID: " + id);
    }

    public TransactionNotFoundException(String referenceNumber) {
        super("Transaction not found with reference number: " + referenceNumber);
    }
}

