package com.kostas.banking.service;

import com.kostas.banking.dto.BalanceCreateDTO;
import com.kostas.banking.dto.BalanceDTO;
import com.kostas.banking.dto.BalanceUpdateDTO;
import com.kostas.banking.exception.AccountNotFoundException;
import com.kostas.banking.exception.BalanceNotFoundException;
import com.kostas.banking.model.Account;
import com.kostas.banking.model.Balance;
import com.kostas.banking.repository.AccountRepository;
import com.kostas.banking.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public BalanceDTO getBalance(UUID id) {
        Balance balance = balanceRepository.findById(id)
                .orElseThrow(() -> new BalanceNotFoundException(id));
        return BalanceDTO.fromEntity(balance);
    }

    @Transactional(readOnly = true)
    public List<BalanceDTO> getAllBalances() {
        return balanceRepository.findAll().stream()
                .map(BalanceDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BalanceDTO> getBalancesByAccountId(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }
        return balanceRepository.findByAccountId(accountId).stream()
                .map(BalanceDTO::fromEntity)
                .toList();
    }

    @Transactional
    public BalanceDTO createBalance(BalanceCreateDTO dto) {
        Account account = accountRepository.findById(dto.accountId())
                .orElseThrow(() -> new AccountNotFoundException(dto.accountId()));

        Balance balance = new Balance();
        balance.setId(UUID.randomUUID());
        balance.setAccount(account);
        balance.setAmount(dto.amount());
        balance.setCurrency(dto.currency());

        Balance saved = balanceRepository.save(balance);
        return BalanceDTO.fromEntity(saved);
    }

    @Transactional
    public BalanceDTO updateBalance(UUID id, BalanceUpdateDTO dto) {
        Balance balance = balanceRepository.findById(id)
                .orElseThrow(() -> new BalanceNotFoundException(id));

        balance.setAmount(dto.amount());

        return BalanceDTO.fromEntity(balance);
    }

    @Transactional
    public void deleteBalance(UUID id) {
        if (!balanceRepository.existsById(id)) {
            throw new BalanceNotFoundException(id);
        }
        balanceRepository.deleteById(id);
    }
}

