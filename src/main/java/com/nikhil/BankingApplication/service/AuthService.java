package com.nikhil.BankingApplication.service;

import com.nikhil.BankingApplication.dto.CustomerLoginDTO;
import com.nikhil.BankingApplication.dto.CustomerRegistrationDTO;

public interface AuthService {
    void register(CustomerRegistrationDTO request);
    String login(CustomerLoginDTO request);
}
