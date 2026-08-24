package com.nikhil.BankingApplication.service.impl;

import com.nikhil.BankingApplication.dto.TransactionHistoryDTO;
import com.nikhil.BankingApplication.entity.Account;
import com.nikhil.BankingApplication.entity.Customer;
import com.nikhil.BankingApplication.exception.AccountNotFoundException;
import com.nikhil.BankingApplication.exception.AccountOwnershipException;
import com.nikhil.BankingApplication.exception.BankingException;
import com.nikhil.BankingApplication.repository.AccountRepository;
import com.nikhil.BankingApplication.repository.CustomerRepository;
import com.nikhil.BankingApplication.service.TransactionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {


    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public TransactionServiceImpl(AccountRepository accountRepository,
                                  CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<TransactionHistoryDTO> getTransactionHistory(String accountNumber,String email){
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new BankingException("Customer not found"));

        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Wrong account number: " + accountNumber));

        if (!account.getOwner().getEmail().equals(email)) {
            throw new AccountOwnershipException();
        }

        return account.getTransactions().stream()
                .map(tx -> new TransactionHistoryDTO(
                        tx.getId(),
                        tx.getType().name(),
                        tx.getAmount(),
                        tx.getTimestamp(),
                        tx.getDescription(),
                        tx.getBalanceAfterTransaction(),
                        tx.getStatus().name()
                ))
                .toList();
    }
}
