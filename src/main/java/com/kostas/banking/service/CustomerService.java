package com.kostas.banking.service;

import com.kostas.banking.dto.CustomerCreateDTO;
import com.kostas.banking.dto.CustomerDTO;
import com.kostas.banking.dto.CustomerUpdateDTO;
import com.kostas.banking.exception.EmailAlreadyExistsException;
import com.kostas.banking.exception.CustomerNotFoundException;
import com.kostas.banking.exception.InvalidCredentialsException;
import com.kostas.banking.model.Customer;
import com.kostas.banking.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public CustomerDTO getCustomer(UUID id) {
        log.info("Fetching customer with id: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return CustomerDTO.fromEntity(customer);
    }

    @Transactional(readOnly = true)
    public CustomerDTO getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(CustomerDTO::fromEntity)
                .orElseThrow(() -> new CustomerNotFoundException(email));
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(CustomerDTO::fromEntity)
                .toList();
    }

    @Transactional
    public CustomerDTO updateCustomer(UUID id, CustomerUpdateDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customer.setFullName(dto.fullName());
        customer.setEmail(dto.email());

        return CustomerDTO.fromEntity(customer);
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }
        customerRepository.deleteById(id);
    }

    @Transactional
    public CustomerDTO createCustomer(CustomerCreateDTO dto) {
        log.info("Creating customer with email: {}", dto.email());
        if (customerRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFullName(dto.fullName());
        customer.setEmail(dto.email());
        customer.setDateOfBirth(dto.dateOfBirth());
        customer.setPassword(dto.password());

        Customer saved = customerRepository.save(customer);
        return CustomerDTO.fromEntity(saved);
    }

    public Customer authenticate(String email, String password) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!customer.verifyPassword(password)) {
            throw new InvalidCredentialsException();
        }

        return customer;
    }
}
