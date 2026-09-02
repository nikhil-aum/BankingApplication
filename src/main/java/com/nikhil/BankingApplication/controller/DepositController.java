package com.nikhil.BankingApplication.controller;

import com.nikhil.BankingApplication.dto.TransactionRequestDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.service.DepositService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deposit")
@Tag(name = " Deposit", description = "Deposit APIs")
public class DepositController {
    private final DepositService depositService;
    private static final Logger logger = LoggerFactory.getLogger(DepositController.class);


    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping
    @Operation(summary = "Deposit money")

    public ResponseEntity<TransactionResultDTO> deposit(@Valid @RequestBody TransactionRequestDTO request, Authentication authentication) {

        String email = authentication.getName();
        logger.info("Deposit request received for account {} by user {} with amount {}",
                request.getAccountNumber(), email, request.getAmount());

        TransactionResultDTO response = depositService.deposit(request,email);
        return ResponseEntity.ok(response);
    }
}
