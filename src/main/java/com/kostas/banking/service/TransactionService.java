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
        }

        if (dto.creditAccountId() != null) {
            creditAccount = accountRepository.findById(dto.creditAccountId())
                    .orElseThrow(() -> new AccountNotFoundException(dto.creditAccountId()));
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

