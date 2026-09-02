package com.nikhil.BankingApplication.controller;

import com.nikhil.BankingApplication.dto.TransactionHistoryDTO;
import com.nikhil.BankingApplication.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Transactions", description = "Transaction APIs")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{accountNumber}/transactions")
    @Operation(summary = "Get All Transaction History")
    public ResponseEntity<List<TransactionHistoryDTO>> getTransactionHistory(
            @PathVariable String accountNumber,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Authentication authentication) {

        String email = authentication.getName();
        List<TransactionHistoryDTO> response = transactionService.getTransactionHistory(
                accountNumber, email, type, status, amount, from, to);

        return ResponseEntity.ok(response);
    }
}
