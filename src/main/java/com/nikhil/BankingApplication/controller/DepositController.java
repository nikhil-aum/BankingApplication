package com.nikhil.BankingApplication.controller;

import com.nikhil.BankingApplication.dto.DepositWithdrawRequestRequest;
import com.nikhil.BankingApplication.dto.DepositWithdrawResponse;
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
    public ResponseEntity<DepositWithdrawResponse> deposit(@Valid @RequestBody DepositWithdrawRequestRequest request) {
        DepositWithdrawResponse response = depositService.deposit(request);
        return ResponseEntity.ok(response);
    }
}
