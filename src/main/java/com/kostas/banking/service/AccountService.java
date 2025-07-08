package com.kostas.banking.service;

import com.kostas.banking.dto.AccountCreateDTO;
import com.kostas.banking.dto.AccountDTO;
import com.kostas.banking.dto.AccountUpdateDTO;
import com.kostas.banking.exception.AccountNotFoundException;
import com.kostas.banking.exception.CustomerNotFoundException;
import com.kostas.banking.model.Account;
import com.kostas.banking.model.Customer;
import com.kostas.banking.repository.AccountRepository;
import com.kostas.banking.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public AccountDTO getAccount(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return AccountDTO.fromEntity(account);
    }

    @Transactional(readOnly = true)
    public AccountDTO getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(AccountDTO::fromEntity)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    @Transactional(readOnly = true)
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(AccountDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountsByCustomerId(UUID customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
        return accountRepository.findByOwnerId(customerId).stream()
                .map(AccountDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AccountDTO createAccount(AccountCreateDTO dto) {
        Customer owner = customerRepository.findById(dto.ownerId())
                .orElseThrow(() -> new CustomerNotFoundException(dto.ownerId()));

        Account account = new Account();
        account.setId(UUID.randomUUID());
        account.setAccountNumber(dto.accountNumber());
        account.setType(dto.type());
        account.setOwner(owner);

        Account saved = accountRepository.save(account);
        return AccountDTO.fromEntity(saved);
    }

    @Transactional
    public AccountDTO updateAccount(UUID id, AccountUpdateDTO dto) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        account.setType(dto.type());

        return AccountDTO.fromEntity(account);
    }

    @Transactional
    public void deleteAccount(UUID id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException(id);
        }
        accountRepository.deleteById(id);
    }
}

