package com.nikhil.BankingApplication.service;

import com.nikhil.BankingApplication.dto.TransactionHistoryDTO;

import java.util.List;

public interface TransactionService {
    public List<TransactionHistoryDTO> getTransactionHistory(
            String accountNumber,
            String email,
            String type,
            String status,
            Double balance,
            String from,
            String to);
}
