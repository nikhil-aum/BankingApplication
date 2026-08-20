package com.nikhil.BankingApplication.controller;

import com.nikhil.BankingApplication.dto.TransactionRequestDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.service.DepositService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deposit")
public class DepositController {
    private final DepositService depositService;

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping
    public ResponseEntity<TransactionResultDTO> deposit(@Valid @RequestBody TransactionRequestDTO request) {
        TransactionResultDTO response = depositService.deposit(request);
        return ResponseEntity.ok(response);
    }
}
