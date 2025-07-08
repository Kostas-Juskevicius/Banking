package com.kostas.banking.dto;

import com.kostas.banking.enums.AccountType;

import java.util.UUID;

public record AccountCreateDTO(
        String accountNumber,
        AccountType type,
        UUID ownerId
) {}

