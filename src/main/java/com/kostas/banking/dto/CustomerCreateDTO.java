package com.kostas.banking.dto;

import java.time.LocalDate;

public record CustomerCreateDTO(
        String fullName,
        String email,
        LocalDate dateOfBirth,
        String password
) {}