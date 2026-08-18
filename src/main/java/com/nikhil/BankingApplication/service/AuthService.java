package com.nikhil.BankingApplication.service;

import com.nikhil.BankingApplication.dto.LoginRequest;
import com.nikhil.BankingApplication.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
    String login(LoginRequest request);
}
