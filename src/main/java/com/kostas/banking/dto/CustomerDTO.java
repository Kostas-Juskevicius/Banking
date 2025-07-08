package com.kostas.banking.dto;

import com.kostas.banking.model.Customer;

import java.time.LocalDate;
import java.util.UUID;

public record CustomerDTO(
        UUID id,
        String fullName,
        String email,
        LocalDate dateOfBirth
) {
    public static CustomerDTO fromEntity(Customer customer) {
        return new CustomerDTO(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getDateOfBirth()
        );
    }
}