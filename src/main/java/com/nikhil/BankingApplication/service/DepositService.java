package com.nikhil.BankingApplication.service;

import com.nikhil.BankingApplication.dto.TransactionRequestDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;

public interface DepositService {
    TransactionResultDTO deposit(TransactionRequestDTO request,String email);
}
