package com.nikhil.BankingApplication.service;


import com.nikhil.BankingApplication.dto.AccountDetailsDTO;
import com.nikhil.BankingApplication.dto.AccountListDTO;
import com.nikhil.BankingApplication.dto.CreateAccountDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;

import java.util.List;

public interface AccountService {
     AccountDetailsDTO createAccount(CreateAccountDTO request, String email);
     TransactionResultDTO checkBalance(String accountNumber, String email);
      List<AccountListDTO> getMyAccounts(String email);
}
