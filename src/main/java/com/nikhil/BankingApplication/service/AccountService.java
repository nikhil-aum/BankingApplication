package com.nikhil.BankingApplication.service;

import com.nikhil.BankingApplication.dto.AccountRequest;
import com.nikhil.BankingApplication.dto.AccountResponse;

public interface AccountService {
     AccountResponse createAccount(AccountRequest request, String email);

}
