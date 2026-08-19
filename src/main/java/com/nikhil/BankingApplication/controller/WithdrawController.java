package com.nikhil.BankingApplication.controller;

import com.nikhil.BankingApplication.dto.DepositWithdrawRequestRequest;
import com.nikhil.BankingApplication.dto.DepositWithdrawResponse;
import com.nikhil.BankingApplication.service.WithdrawService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/withdraw")
public class WithdrawController {
    private final WithdrawService withdrawService;

    public WithdrawController(WithdrawService withdrawService) {
        this.withdrawService = withdrawService;
    }

    @PostMapping
    public ResponseEntity<DepositWithdrawResponse> deposit(@Valid @RequestBody DepositWithdrawRequestRequest request) {
        DepositWithdrawResponse response = withdrawService.withdraw(request);
        return ResponseEntity.ok(response);
    }
}
