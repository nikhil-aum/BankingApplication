package com.nikhil.BankingApplication.service.impl;

import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.repository.AccountRepository;
import com.nikhil.BankingApplication.repository.CustomerRepository;
import com.nikhil.BankingApplication.repository.TransactionRepository;
import com.nikhil.BankingApplication.service.TransactionService;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public TransactionResultDTO getTransationHistory(String accountNumber){

    }



    @Override
    public TransactionResultDTO getTransactionHistory(String accountNumber) throws AccountNotFoundException {

    }
}
