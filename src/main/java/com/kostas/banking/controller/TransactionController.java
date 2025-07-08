package com.kostas.banking.controller;

import com.kostas.banking.dto.TransactionCreateDTO;
import com.kostas.banking.dto.TransactionDTO;
import com.kostas.banking.dto.TransactionUpdateDTO;
import com.kostas.banking.enums.TransactionStatus;
import com.kostas.banking.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getTransaction(id));
    }

    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<TransactionDTO> getTransactionByReferenceNumber(@PathVariable String referenceNumber) {
        return ResponseEntity.ok(transactionService.getTransactionByReferenceNumber(referenceNumber));
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/account/{accountDebitId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByDebitAccountId(@PathVariable UUID accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByDebitAccountId(accountId));
    }

    @GetMapping("/account/{accountCreditId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByCreditAccountId(@PathVariable UUID accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByCreditAccountId(accountId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByStatus(@PathVariable TransactionStatus status) {
        return ResponseEntity.ok(transactionService.getTransactionsByStatus(status));
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody @Valid TransactionCreateDTO transactionCreateDTO) {
        TransactionDTO created = transactionService.createTransaction(transactionCreateDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable UUID id,
            @RequestBody @Valid TransactionUpdateDTO transactionUpdateDTO) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, transactionUpdateDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable UUID id) {
        transactionService.deleteTransaction(id);
    }
}

