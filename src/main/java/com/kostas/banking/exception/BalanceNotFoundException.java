package com.kostas.banking.exception;

import java.util.UUID;

public class BalanceNotFoundException extends RuntimeException {
    public BalanceNotFoundException(UUID id) {
        super("Balance not found with ID: " + id);
    }
}

