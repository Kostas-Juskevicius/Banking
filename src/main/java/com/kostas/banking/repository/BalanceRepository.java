package com.kostas.banking.repository;

import com.kostas.banking.model.Balance;
import com.kostas.banking.enums.CurrencyCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, UUID> {
    List<Balance> findByAccountId(UUID accountId);
    List<Balance> findByAccountOwnerId(UUID ownerId);
    Optional<Balance> findByAccountIdAndCurrency(UUID accountId, CurrencyCode currency);
    boolean existsByAccountIdAndCurrency(UUID accountId, CurrencyCode currency);
}