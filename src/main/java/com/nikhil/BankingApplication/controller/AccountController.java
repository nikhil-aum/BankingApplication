package com.nikhil.BankingApplication.controller;


import com.nikhil.BankingApplication.dto.AccountDetailsDTO;
import com.nikhil.BankingApplication.dto.CreateAccountDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "Check Acount balance")
    public ResponseEntity<TransactionResultDTO> checkBalance(@PathVariable String accountNumber, Authentication authentication){
        String email = authentication.getName();
        TransactionResultDTO response = accountService.checkBalance(accountNumber,email);
        return ResponseEntity.ok(response);
    }
}
