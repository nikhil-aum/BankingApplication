package com.nikhil.BankingApplication.service;

import com.nikhil.BankingApplication.dto.TransactionHistoryDTO;

import java.util.List;

public interface TransactionService {
    List<TransactionHistoryDTO> getTransactionHistory(String accountNumber, String email);
    List<TransactionHistoryDTO> getTransactionHistoryByTypeDeposit(String accountNumber, String email);
    List<TransactionHistoryDTO> getTransactionHistoryByTypeWithdraw(String accountNumber, String email);
    List<TransactionHistoryDTO> getTransactionHistoryByTypeTransferIn(String accountNumber, String email);
    List<TransactionHistoryDTO> getTransactionHistoryByTypeTransferOut(String accountNumber, String email);
    List<TransactionHistoryDTO> getTransactionHistoryByBalance(double amount, String email);
    List<TransactionHistoryDTO> getTransactionHistoryByTime(String from, String to, String email);
    List<TransactionHistoryDTO> getTransactionHistoryByStatus(String status,String email);

}
