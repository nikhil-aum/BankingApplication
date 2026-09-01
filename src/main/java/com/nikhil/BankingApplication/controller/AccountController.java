package com.nikhil.BankingApplication.controller;


import com.nikhil.BankingApplication.dto.AccountDetailsDTO;
import com.nikhil.BankingApplication.dto.AccountListDTO;
import com.nikhil.BankingApplication.dto.CreateAccountDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }


    @PostMapping("/open")
    @Operation(summary = "Open account")
    public ResponseEntity<AccountDetailsDTO> createAccount(@Valid @RequestBody CreateAccountDTO request, Authentication authentication){
        String email = authentication.getName();

        AccountDetailsDTO account = accountService.createAccount(request,email);

        return ResponseEntity.ok(account);
    }


    @GetMapping("/{accountNumber}")
    @Operation(summary = "Check Account balance")
    public ResponseEntity<TransactionResultDTO> checkBalance(@PathVariable String accountNumber, Authentication authentication){
        String email = authentication.getName();
        TransactionResultDTO response = accountService.checkBalance(accountNumber,email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-accounts")
    @Operation(summary = "Get all accounts of logged-in customer")
    public ResponseEntity<?> getMyAccounts(Authentication authentication) {
        String email = authentication.getName();

            List<AccountListDTO> response = accountService.getMyAccounts(email);
            return ResponseEntity.ok(response);
        }
    }

