package com.kostas.banking.controller;

import com.kostas.banking.dto.BalanceCreateDTO;
import com.kostas.banking.dto.BalanceDTO;
import com.kostas.banking.dto.BalanceUpdateDTO;
import com.kostas.banking.service.BalanceService;
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
@RequestMapping("/api/balances")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/{id}")
    public ResponseEntity<BalanceDTO> getBalance(@PathVariable UUID id) {
        return ResponseEntity.ok(balanceService.getBalance(id));
    }

    @GetMapping
    public ResponseEntity<List<BalanceDTO>> getAllBalances() {
        return ResponseEntity.ok(balanceService.getAllBalances());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<BalanceDTO>> getBalancesByAccountId(@PathVariable UUID accountId) {
        return ResponseEntity.ok(balanceService.getBalancesByAccountId(accountId));
    }

    @PostMapping
    public ResponseEntity<BalanceDTO> createBalance(@RequestBody @Valid BalanceCreateDTO balanceCreateDTO) {
        BalanceDTO created = balanceService.createBalance(balanceCreateDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BalanceDTO> updateBalance(
            @PathVariable UUID id,
            @RequestBody @Valid BalanceUpdateDTO balanceUpdateDTO) {
        return ResponseEntity.ok(balanceService.updateBalance(id, balanceUpdateDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBalance(@PathVariable UUID id) {
        balanceService.deleteBalance(id);
    }
}

