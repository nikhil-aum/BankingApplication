package com.nikhil.BankingApplication.service;

import com.nikhil.BankingApplication.dto.TransactionRequestDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;

public interface WithdrawService {

    TransactionResultDTO withdraw(TransactionRequestDTO request,String email);
}
