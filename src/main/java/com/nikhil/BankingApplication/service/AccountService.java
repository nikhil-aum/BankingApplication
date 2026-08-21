package com.nikhil.BankingApplication.service;

<<<<<<< HEAD
import com.nikhil.BankingApplication.dto.AccountDetailsDTO;
import com.nikhil.BankingApplication.dto.CreateAccountDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;

public interface AccountService {
     AccountDetailsDTO createAccount(CreateAccountDTO request, String email);
     TransactionResultDTO checkBalance(String accountNumber, String email);
=======
import com.nikhil.BankingApplication.dto.CreateAccountDTO;
import com.nikhil.BankingApplication.dto.AccountDetailsDTO;

public interface AccountService {
     AccountDetailsDTO createAccount(CreateAccountDTO request, String email);

>>>>>>> main
}
