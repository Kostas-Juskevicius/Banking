package com.kostas.banking.controller;

import com.kostas.banking.dto.AuthRequest;
import com.kostas.banking.dto.AuthResponse;
import com.kostas.banking.model.Customer;
import com.kostas.banking.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomerService customerService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        Customer customer = customerService.authenticate(
                request.email(),
                request.password()
        );

        // TODO maybe JWT token
        return ResponseEntity.ok(new AuthResponse(
                "Authentication successful",
                customer.getId()
        ));
    }
}