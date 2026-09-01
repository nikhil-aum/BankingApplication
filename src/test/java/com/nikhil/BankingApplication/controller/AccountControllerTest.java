package com.nikhil.BankingApplication.controller;

import com.nikhil.BankingApplication.dto.AccountDetailsDTO;
import com.nikhil.BankingApplication.dto.AccountListDTO;
import com.nikhil.BankingApplication.dto.CreateAccountDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.service.AccountService;
import com.nikhil.BankingApplication.service.impl.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "ram@gmail.com")
    void testCreateAccount() throws Exception {
        AccountDetailsDTO response = new AccountDetailsDTO();
        response.setAccountNumber("123456789012");
        response.setAccountType("SAVING");
        response.setBalance(BigDecimal.ZERO);
        response.setOwner("Test User");

        when(accountService.createAccount(any(CreateAccountDTO.class), eq("ram@gmail.com")))
                .thenReturn(response);

        mockMvc.perform(post("/api/accounts/open")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountType\":\"SAVING\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("123456789012"))
                .andExpect(jsonPath("$.accountType").value("SAVING"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    @WithMockUser(username = "ram@example.com")
    void testCheckBalance() throws Exception {
        TransactionResultDTO result = new TransactionResultDTO();
        result.setMessage("Balance in your Account : 1000");

        when(accountService.checkBalance("123456789012", "ram@example.com"))
                .thenReturn(result);

        mockMvc.perform(get("/api/accounts/123456789012"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Balance in your Account : 1000"));
    }

    @Test
    @WithMockUser(username = "ram@example.com")
    void testGetMyAccounts() throws Exception {

        AccountListDTO account1 = new AccountListDTO();
        account1.setAccountNumber("123456789012");
        account1.setAccountType("SAVING");
        account1.setBalance(BigDecimal.valueOf(1000));

        AccountListDTO account2 = new AccountListDTO();
        account2.setAccountNumber("987654321098");
        account2.setAccountType("CURRENT");
        account2.setBalance(BigDecimal.valueOf(5000));

        when(accountService.getMyAccounts("ram@example.com"))
                .thenReturn(List.of(account1, account2));

        mockMvc.perform(get("/api/accounts/my-accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].accountNumber")
                        .value("123456789012"))
                .andExpect(jsonPath("$[0].accountType")
                        .value("SAVING"))
                .andExpect(jsonPath("$[0].balance")
                        .value(1000))

                .andExpect(jsonPath("$[1].accountNumber")
                        .value("987654321098"))
                .andExpect(jsonPath("$[1].accountType")
                        .value("CURRENT"))
                .andExpect(jsonPath("$[1].balance")
                        .value(5000));
    }



}
