package com.kostas.banking.exception;

public record ErrorResponse(
        int status,
        String error,
        String message
) {}