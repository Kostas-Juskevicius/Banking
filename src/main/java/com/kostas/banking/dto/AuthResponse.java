package com.kostas.banking.dto;

import java.util.UUID;

public record AuthResponse(
        String message,
        UUID customerId
) {}