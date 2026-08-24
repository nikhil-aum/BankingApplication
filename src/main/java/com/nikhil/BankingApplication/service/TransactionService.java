package com.nikhil.BankingApplication.service;

import com.nikhil.BankingApplication.dto.TransactionResultDTO;

import javax.security.auth.login.AccountNotFoundException;

public interface TransactionService {
    TransactionResultDTO getTransactionHistory(String accountNumber) throws AccountNotFoundException;
}

