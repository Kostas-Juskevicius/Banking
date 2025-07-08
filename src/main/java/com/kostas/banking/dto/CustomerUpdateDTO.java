package com.kostas.banking.dto;

import com.kostas.banking.model.Customer;

public record CustomerUpdateDTO(
        String fullName,
        String email
) {
    public static CustomerUpdateDTO fromEntity(Customer customer) {
        return new CustomerUpdateDTO(
                customer.getFullName(),
                customer.getEmail()
        );
    }
}