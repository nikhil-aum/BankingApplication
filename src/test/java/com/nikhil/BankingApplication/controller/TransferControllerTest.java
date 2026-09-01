package com.nikhil.BankingApplication.controller;

import com.nikhil.BankingApplication.dto.MoneyTransferDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.exception.BankingException;
import com.nikhil.BankingApplication.service.TransferService;
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

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferService transferService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "ramabc@example.com")
    void transfer_success() throws Exception {

        TransactionResultDTO response = new TransactionResultDTO();
        response.setMessage("₹1000 transferred successfully from ram to shyam");

        when(transferService.transfer(
                any(MoneyTransferDTO.class),
                eq("ramabc@example.com")))
                .thenReturn(response);

        mockMvc.perform(post("/api/transfer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "senderAccountNumber":"111",
                                  "recipientAccountNumber":"222",
                                  "confirmRecipientAccountNumber":"222",
                                  "amount":1000
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("₹1000 transferred successfully from ram to shyam"));
    }

    @Test
    @WithMockUser(username = "ramabc@example.com")
    void transfer_recipientMismatch() throws Exception {

        when(transferService.transfer(
                any(MoneyTransferDTO.class),
                eq("ramabc@example.com")))
                .thenThrow(
                        new BankingException("Recipient account numbers do not match"));

        mockMvc.perform(post("/api/transfer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "senderAccountNumber":"111",
                                  "recipientAccountNumber":"222",
                                  "confirmRecipientAccountNumber":"999",
                                  "amount":1000
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
