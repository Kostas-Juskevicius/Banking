package com.kostas.banking.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(UUID id) {
        super("Account not found with ID: " + id);
    }

    public AccountNotFoundException(String accountNumber) {
        super("Account not found with account number: " + accountNumber);
    }
}

