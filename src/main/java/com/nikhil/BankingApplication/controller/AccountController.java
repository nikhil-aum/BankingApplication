package com.nikhil.BankingApplication.controller;

<<<<<<< HEAD
import com.nikhil.BankingApplication.dto.AccountDetailsDTO;
import com.nikhil.BankingApplication.dto.CreateAccountDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "2. Accounts", description = "Account creation APIs")
=======
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
>>>>>>> main
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }

<<<<<<< HEAD
    @PostMapping("/open")
    @Operation(summary = "Open account")
=======
    @PostMapping("/create")
>>>>>>> main
    public ResponseEntity<AccountDetailsDTO> createAccount(@Valid @RequestBody CreateAccountDTO request, Authentication authentication){
        String email = authentication.getName();

        AccountDetailsDTO account = accountService.createAccount(request,email);

        return ResponseEntity.ok(account);
    }
<<<<<<< HEAD

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Check Acount balance")
    public ResponseEntity<TransactionResultDTO> checkBalance(@PathVariable String accountNumber, Authentication authentication){
        String email = authentication.getName();
        TransactionResultDTO response = accountService.checkBalance(accountNumber,email);
        return ResponseEntity.ok(response);
    }
=======
>>>>>>> main
}
