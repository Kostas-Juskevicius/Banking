package com.kostas.banking.controller;

import com.kostas.banking.dto.AccountCreateDTO;
import com.kostas.banking.dto.AccountDTO;
import com.kostas.banking.dto.AccountUpdateDTO;
import com.kostas.banking.service.AccountService;
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
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    @GetMapping("/account-number/{accountNumber}")
    public ResponseEntity<AccountDTO> getAccountByAccountNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountByAccountNumber(accountNumber));
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByCustomerId(@PathVariable UUID customerId) {
        return ResponseEntity.ok(accountService.getAccountsByCustomerId(customerId));
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody @Valid AccountCreateDTO accountCreateDTO) {
        AccountDTO created = accountService.createAccount(accountCreateDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(
            @PathVariable UUID id,
            @RequestBody @Valid AccountUpdateDTO accountUpdateDTO) {
        return ResponseEntity.ok(accountService.updateAccount(id, accountUpdateDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable UUID id) {
        accountService.deleteAccount(id);
    }
}

