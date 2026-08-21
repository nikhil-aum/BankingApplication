package com.nikhil.BankingApplication.service;

import com.nikhil.BankingApplication.dto.CreateAccountDTO;
import com.nikhil.BankingApplication.dto.AccountDetailsDTO;

public interface AccountService {
     AccountDetailsDTO createAccount(CreateAccountDTO request, String email);

}
