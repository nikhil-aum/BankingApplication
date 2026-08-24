package com.nikhil.BankingApplication.controller;

import com.nikhil.BankingApplication.dto.AuthenticationResultDTO;
import com.nikhil.BankingApplication.dto.CustomerLoginDTO;
import com.nikhil.BankingApplication.dto.CustomerRegistrationDTO;
import com.nikhil.BankingApplication.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register and Login APIs")
public class AuthController {
    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    @Operation(summary = "Register new customer")
    public ResponseEntity<String> register(@Valid @RequestBody CustomerRegistrationDTO request){
         authService.register(request);
         return  ResponseEntity.status(HttpStatus.CREATED)
                 .body("Customer registered successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "Login customer")
    public ResponseEntity<AuthenticationResultDTO> login(@Valid @RequestBody CustomerLoginDTO request){
        String token = authService.login(request);
        return  ResponseEntity.ok(new AuthenticationResultDTO(token,"Login successful"));
    }

}