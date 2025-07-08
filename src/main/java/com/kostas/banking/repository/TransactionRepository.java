package com.kostas.banking.repository;

import com.kostas.banking.model.Transaction;
import com.kostas.banking.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByReferenceNumber(String referenceNumber);
    boolean existsByReferenceNumber(String referenceNumber);
    List<Transaction> findByDebitAccountId(UUID accountId);
    List<Transaction> findByCreditAccountId(UUID accountId);
    List<Transaction> findByStatus(TransactionStatus status);
}