package com.kostas.banking.dto;

public record AuthRequest(
        String email,
        String password
) {}