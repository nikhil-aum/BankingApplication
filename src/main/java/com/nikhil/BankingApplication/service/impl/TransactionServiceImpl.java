package com.nikhil.BankingApplication.service.impl;

import com.nikhil.BankingApplication.dto.TransactionHistoryDTO;
import com.nikhil.BankingApplication.entity.Account;
import com.nikhil.BankingApplication.entity.Customer;
import com.nikhil.BankingApplication.entity.TransactionStatus;
import com.nikhil.BankingApplication.entity.TransactionType;
import com.nikhil.BankingApplication.exception.AccountNotFoundException;
import com.nikhil.BankingApplication.exception.AccountOwnershipException;
import com.nikhil.BankingApplication.exception.BankingException;
import com.nikhil.BankingApplication.repository.AccountRepository;
import com.nikhil.BankingApplication.repository.CustomerRepository;
import com.nikhil.BankingApplication.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);


    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public TransactionServiceImpl(AccountRepository accountRepository,
                                  CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<TransactionHistoryDTO> getTransactionHistory(
            String accountNumber,
            String email,
            String type,
            String status,
            Double balance,
            String from,
            String to) {

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new BankingException("Customer not found"));

        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Wrong account number: " + accountNumber));

        if (!account.getOwner().getEmail().equals(email)) {
            throw new AccountOwnershipException();
        }

        return account.getTransactions().stream()
                .filter(tx -> {
                    boolean matches = true;

                    if (type != null) {
                        try {
                            TransactionType txnType = TransactionType.valueOf(type.toUpperCase());
                            matches = matches && tx.getType() == txnType;
                        } catch (IllegalArgumentException e) {
                            throw new BankingException("Invalid transaction type: " + type);
                        }
                    }

                    if (status != null) {
                        try {
                            TransactionStatus txnStatus = TransactionStatus.valueOf(status.toUpperCase());
                            matches = matches && tx.getStatus() == txnStatus;
                        } catch (IllegalArgumentException e) {
                            throw new BankingException("Invalid status: " + status);
                        }
                    }

                    if (balance != null) {
                        matches = matches && tx.getBalanceAfterTransaction().doubleValue() < balance;
                    }

                    if (from != null && to != null) {
                        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
                        LocalTime fromTime = LocalTime.parse(from, timeFmt);
                        LocalTime toTime = LocalTime.parse(to, timeFmt);
                        LocalTime txnTime = tx.getTimestamp().toLocalTime();
                        matches = matches && !txnTime.isBefore(fromTime) && !txnTime.isAfter(toTime);
                    }

                    return matches;
                })
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
