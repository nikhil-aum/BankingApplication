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

    @GetMapping("/{accountNumber}/deposit-history")
    @Operation(summary = "Get Deposit Transaction History")
    public ResponseEntity<List<TransactionHistoryDTO>> getDepositHistory(@PathVariable String accountNumber,Authentication authentication) {
        String email = authentication.getName();
        List<TransactionHistoryDTO> response = transactionService.getTransactionHistoryByTypeDeposit(accountNumber, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountNumber}/withdraw-history")
    @Operation(summary = "Get Withdraw Transaction History")
    public ResponseEntity<List<TransactionHistoryDTO>> getWithdrawHistory(@PathVariable String accountNumber,Authentication authentication) {
        String email = authentication.getName();
        List<TransactionHistoryDTO> response = transactionService.getTransactionHistoryByTypeWithdraw(accountNumber, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountNumber}/transferIn-history")
    @Operation(summary = "Get Transfer-In Transaction History")
    public ResponseEntity<List<TransactionHistoryDTO>> getTransferInHistory(@PathVariable String accountNumber,Authentication authentication) {
        String email = authentication.getName();
        List<TransactionHistoryDTO> response = transactionService.getTransactionHistoryByTypeTransferIn(accountNumber, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountNumber}/transferOut-history")
    @Operation(summary = "Get Transfer-Out Transaction History")
    public ResponseEntity<List<TransactionHistoryDTO>> getTransferOutHistory(@PathVariable String accountNumber,Authentication authentication) {
        String email = authentication.getName();
        List<TransactionHistoryDTO> response = transactionService.getTransactionHistoryByTypeTransferOut(accountNumber, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{amount}/balance-history")
    public ResponseEntity<List<TransactionHistoryDTO>> getTransactionHistoryByBalance(@PathVariable double amount,Authentication authentication){
        String email = authentication.getName();
        List<TransactionHistoryDTO> response = transactionService.getTransactionHistoryByBalance(amount,email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/time-history")
    @Operation(summary = "Get Transaction History by Time Range")
    public ResponseEntity<?> getTransactionHistoryByTime(@RequestParam String from,
                                                         @RequestParam String to,
                                                         Authentication authentication) {
        String email = authentication.getName();
        List<TransactionHistoryDTO> response = transactionService.getTransactionHistoryByTime(from, to, email);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/status-history")
    @Operation(summary = "Get Transaction History by Status")
    public ResponseEntity<?> getTransactionHistoryByStatus(@RequestParam String status,
                                                           Authentication authentication) {
        String email = authentication.getName();
        List<TransactionHistoryDTO> response = transactionService.getTransactionHistoryByStatus(status, email);

        return ResponseEntity.ok(response);

    }

}
