package com.nikhil.BankingApplication.controller;

import com.nikhil.BankingApplication.dto.TransactionRequestDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.service.WithdrawService;
<<<<<<< HEAD
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
=======
>>>>>>> main
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/withdraw")
<<<<<<< HEAD
@Tag(name = "4. Withdraw", description = "Withdraw APIs")
=======
>>>>>>> main
public class WithdrawController {
    private final WithdrawService withdrawService;

    public WithdrawController(WithdrawService withdrawService) {
        this.withdrawService = withdrawService;
    }

    @PostMapping
<<<<<<< HEAD
    @Operation(summary = "Withdraw money")
=======
>>>>>>> main
    public ResponseEntity<TransactionResultDTO> deposit(@Valid @RequestBody TransactionRequestDTO request) {
        TransactionResultDTO response = withdrawService.withdraw(request);
        return ResponseEntity.ok(response);
    }
}
