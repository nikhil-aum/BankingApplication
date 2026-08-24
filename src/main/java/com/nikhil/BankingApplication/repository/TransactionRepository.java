package com.nikhil.BankingApplication.repository;

import com.nikhil.BankingApplication.entity.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository {
    List<Transaction> findByAccount_AccountNumber(String accountNumber);
}
