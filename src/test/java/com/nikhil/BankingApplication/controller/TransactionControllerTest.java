package com.nikhil.BankingApplication.controller;

import com.nikhil.BankingApplication.dto.TransactionHistoryDTO;
import com.nikhil.BankingApplication.service.TransactionService;
import com.nikhil.BankingApplication.service.impl.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "testuser@example.com")
    void getTransactionHistory_success_shouldReturn200AndList() throws Exception {
        TransactionHistoryDTO dto = new TransactionHistoryDTO(
                1L,
                "DEPOSIT",
                BigDecimal.valueOf(1000),
                LocalDateTime.now(),
                "Deposit success",
                BigDecimal.valueOf(6000),
                "SUCCESS"
        );

        when(transactionService.getTransactionHistory(eq("123456"), eq("testuser@example.com")))
                .thenReturn(List.of(dto));

        mockMvc.perform(
                        get("/api/accounts/123456/history")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[0].amount").value(1000))
                .andExpect(jsonPath("$[0].description").value("Deposit success"))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    void getTransactionHistory_accountNotFound_shouldReturn404() throws Exception {
        when(transactionService.getTransactionHistory(eq("999999"), eq("testuser@example.com")))
                .thenThrow(new com.nikhil.BankingApplication.exception.AccountNotFoundException("Wrong account number"));

        mockMvc.perform(get("/api/accounts/999999/history")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    void getTransactionHistory_ownershipMismatch_shouldReturn403() throws Exception {
        when(transactionService.getTransactionHistory(eq("123456"), eq("testuser@example.com")))
                .thenThrow(new com.nikhil.BankingApplication.exception.AccountOwnershipException());

        mockMvc.perform(get("/api/accounts/123456/history")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
