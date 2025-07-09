package com.kostas.banking.service;

import com.kostas.banking.dto.TransactionCreateDTO;
import com.kostas.banking.dto.TransactionDTO;
import com.kostas.banking.dto.TransactionUpdateDTO;
import com.kostas.banking.enums.TransactionStatus;
import com.kostas.banking.exception.AccountNotFoundException;
import com.kostas.banking.exception.TransactionNotFoundException;
import com.kostas.banking.model.Account;
import com.kostas.banking.model.Transaction;
import com.kostas.banking.repository.AccountRepository;
import com.kostas.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final BalanceService balanceService;

    @Transactional(readOnly = true)
    public TransactionDTO getTransaction(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        return TransactionDTO.fromEntity(transaction);
    }

    @Transactional(readOnly = true)
    public TransactionDTO getTransactionByReferenceNumber(String referenceNumber) {
        return transactionRepository.findByReferenceNumber(referenceNumber)
                .map(TransactionDTO::fromEntity)
                .orElseThrow(() -> new TransactionNotFoundException(referenceNumber));
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(TransactionDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByDebitAccountId(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }
        return transactionRepository.findByDebitAccountId(accountId).stream()
                .map(TransactionDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByCreditAccountId(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }
        return transactionRepository.findByCreditAccountId(accountId).stream()
                .map(TransactionDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status).stream()
                .map(TransactionDTO::fromEntity)
                .toList();
    }

    @Transactional
    public TransactionDTO createTransaction(TransactionCreateDTO dto) {
        Account debitAccount = null;
        Account creditAccount = null;

        if (dto.debitAccountId() != null) {
            debitAccount = accountRepository.findById(dto.debitAccountId())
                    .orElseThrow(() -> new AccountNotFoundException(dto.debitAccountId()));
            
            // Check if account has sufficient balance for withdrawal/transfer
            if (dto.type().equals(com.kostas.banking.enums.TransactionType.WITHDRAWAL) || 
                dto.type().equals(com.kostas.banking.enums.TransactionType.TRANSFER)) {
                
                // Get current balance for the account in the transaction currency
                List<com.kostas.banking.dto.BalanceDTO> accountBalances = balanceService.getBalancesByAccountId(dto.debitAccountId());
                java.math.BigDecimal currentBalance = java.math.BigDecimal.ZERO;
                
                for (com.kostas.banking.dto.BalanceDTO balance : accountBalances) {
                    if (balance.currency().equals(dto.currency())) {
                        currentBalance = currentBalance.add(balance.amount());
                    }
                }
                
                // Check if sufficient funds are available
                if (currentBalance.compareTo(dto.amount()) < 0) {
                    throw new IllegalArgumentException("Insufficient funds. Available balance: " + currentBalance + " " + dto.currency() + ", Required: " + dto.amount() + " " + dto.currency());
                }
            }
        }

        if (dto.creditAccountId() != null) {
            creditAccount = accountRepository.findById(dto.creditAccountId())
                    .orElseThrow(() -> new AccountNotFoundException(dto.creditAccountId()));
            // For internal transfers, ensure credit account also belongs to the current user
            // For external transfers, this check might be different or not apply
            if (dto.type().equals(com.kostas.banking.enums.TransactionType.TRANSFER) && debitAccount != null && !creditAccount.getOwner().getId().equals(debitAccount.getOwner().getId())) {
                throw new IllegalArgumentException("Cannot transfer to an account not owned by the same customer for internal transfers.");
            }
        }

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setReferenceNumber(dto.referenceNumber());
        transaction.setDebitAccount(debitAccount);
        transaction.setCreditAccount(creditAccount);
        transaction.setAmount(dto.amount());
        transaction.setCurrency(dto.currency());
        transaction.setType(dto.type());
        transaction.setStatus(TransactionStatus.PENDING);

        Transaction saved = transactionRepository.save(transaction);
        return TransactionDTO.fromEntity(saved);
    }

    @Transactional
    public TransactionDTO updateTransaction(UUID id, TransactionUpdateDTO dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        transaction.setStatus(dto.status());
        if (dto.postedAt() != null) {
            transaction.setPostedAt(dto.postedAt());
        } else if (dto.status() == TransactionStatus.COMPLETED) {
            transaction.setPostedAt(LocalDateTime.now());
        }

        return TransactionDTO.fromEntity(transaction);
    }

    @Transactional
    public void deleteTransaction(UUID id) {
        if (!transactionRepository.existsById(id)) {
            throw new TransactionNotFoundException(id);
        }
        transactionRepository.deleteById(id);
    }
}

