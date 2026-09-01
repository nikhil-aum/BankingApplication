package com.nikhil.BankingApplication.controller;

import com.nikhil.BankingApplication.dto.CustomerLoginDTO;
import com.nikhil.BankingApplication.dto.CustomerRegistrationDTO;
import com.nikhil.BankingApplication.security.JwtFilter;
import com.nikhil.BankingApplication.service.AuthService;
import com.nikhil.BankingApplication.service.impl.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Test
    @WithMockUser
    void testRegister_success() throws Exception {

        doNothing().when(authService)
                .register(any(CustomerRegistrationDTO.class));

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name":"Ram",
                              "email":"ram@example.com",
                              "password":"secret123"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(content().string("Customer registered successfully"));
    }

    @Test
    @WithMockUser
    void testLogin_success() throws Exception {

        when(authService.login(any(CustomerLoginDTO.class)))
                .thenReturn("jwt-token-123");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email":"ram@example.com",
                              "password":"secret123"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("jwt-token-123"))
                .andExpect(jsonPath("$.message")
                        .value("Login successful"));
    }




}