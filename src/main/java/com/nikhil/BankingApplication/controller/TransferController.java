package com.nikhil.BankingApplication.controller;

import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.dto.MoneyTransferDTO;
import com.nikhil.BankingApplication.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfer")
@Tag(name = "Transfer", description = "Transfer APIs")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService){
        this.transferService = transferService;
    }

    @PostMapping
    @Operation(summary = "Transfer money")
    public ResponseEntity<TransactionResultDTO> transfer(@Valid @RequestBody MoneyTransferDTO request, Authentication authentication) {

        String email = authentication.getName();
        TransactionResultDTO message = transferService.transfer(request,email);
        return ResponseEntity.ok(message);
    }
}
