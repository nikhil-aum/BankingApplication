package com.nikhil.BankingApplication.controller;

import com.nikhil.BankingApplication.dto.TransactionRequestDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.exception.BankingException;
import com.nikhil.BankingApplication.service.DepositService;
import com.nikhil.BankingApplication.service.impl.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepositController.class)
class DepositControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepositService depositService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "ramabc@example.com")
    void deposit_success_shouldReturn200AndMessage() throws Exception {

        TransactionResultDTO response = new TransactionResultDTO();
        response.setMessage("₹1000 credited successfully in your account");

        when(depositService.deposit(
                any(TransactionRequestDTO.class),
                eq("ramabc@example.com")))
                .thenReturn(response);

        mockMvc.perform(post("/api/deposit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountNumber":"123456",
                                  "confirmAccountNumber":"123456",
                                  "amount":1000
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("₹1000 credited successfully in your account"));
    }

    @Test
    @WithMockUser(username = "ramabc@example.com")
    void deposit_accountMismatch() throws Exception {

        when(depositService.deposit(
                any(TransactionRequestDTO.class),
                eq("ramabc@example.com")))
                .thenThrow(
                        new BankingException("Account numbers do not match"));

        mockMvc.perform(post("/api/deposit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountNumber":"123456",
                                  "confirmAccountNumber":"999999",
                                  "amount":1000
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
