package com.nikhil.BankingApplication.controller;

import com.nikhil.BankingApplication.dto.CreateAccountDTO;
import com.nikhil.BankingApplication.dto.AccountDetailsDTO;
import com.nikhil.BankingApplication.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

    @PostMapping("/create")
    public ResponseEntity<AccountDetailsDTO> createAccount(@Valid @RequestBody CreateAccountDTO request, Authentication authentication){
        String email = authentication.getName();

        AccountDetailsDTO account = accountService.createAccount(request,email);

        return ResponseEntity.ok(account);
    }
}
