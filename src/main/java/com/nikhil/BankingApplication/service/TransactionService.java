package com.nikhil.BankingApplication.service;

import com.nikhil.BankingApplication.dto.TransactionHistoryDTO;

import java.util.List;

public interface TransactionService {
    List<TransactionHistoryDTO> getTransactionHistory(String accountNumber, String email);
}
