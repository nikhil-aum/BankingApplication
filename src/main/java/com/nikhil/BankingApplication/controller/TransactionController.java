package com.nikhil.BankingApplication.controller;

import com.nikhil.BankingApplication.dto.TransactionHistoryDTO;
import com.nikhil.BankingApplication.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = " Transactions", description = "Transaction APIs")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{accountNumber}/history")
    @Operation(summary = "Get Transaction History")
    public ResponseEntity<List<TransactionHistoryDTO>> getTransactionHistory(@PathVariable String accountNumber, Authentication authentication){
        String email = authentication.getName();
        List<TransactionHistoryDTO> transactions = transactionService.getTransactionHistory(accountNumber, email);
        return ResponseEntity.ok(transactions);
    }
}
