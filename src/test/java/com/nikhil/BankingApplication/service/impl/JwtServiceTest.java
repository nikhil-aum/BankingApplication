package com.nikhil.BankingApplication.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtServiceTest {

    private JwtService jwtService;
    private String secret;

    @BeforeEach
    void setup(){
         secret = Base64.getEncoder().encodeToString("mysecretkeymysecretkey1234567890".getBytes());
         jwtService = new JwtService(secret);
    }

    @Test
    void generateToken_success() {
        String token = jwtService.generateToken("nikhil@gmail.com");
        assertNotNull(token);
    }

    @Test
    void extractEmail_success() {
        String token = jwtService.generateToken("nikhil@gmail.com");
        String email = jwtService.extractEmail(token);
        Assertions.assertEquals("nikhil@gmail.com", email);
    }

    @Test
    void validateToken_success() {
        String token = jwtService.generateToken("nikhil@gmail.com");
        boolean isValid = jwtService.validateToken(token, "nikhil@gmail.com");
        Assertions.assertTrue(isValid);
    }

    @Test
    void validateToken_emailMismatch() {
        String token = jwtService.generateToken("nikhil@gmail.com");
        boolean isValid = jwtService.validateToken(token, "ramg@gmail.com");
        Assertions.assertFalse(isValid);
    }


}
