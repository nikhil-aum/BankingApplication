package com.nikhil.BankingApplication.service.impl;

import com.nikhil.BankingApplication.dto.TransactionRequestDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.entity.Account;
import com.nikhil.BankingApplication.entity.Transaction;
import com.nikhil.BankingApplication.entity.TransactionStatus;
import com.nikhil.BankingApplication.entity.TransactionType;
import com.nikhil.BankingApplication.exception.AccountOwnershipException;
import com.nikhil.BankingApplication.exception.BankingException;
import com.nikhil.BankingApplication.repository.AccountRepository;
import com.nikhil.BankingApplication.service.DepositService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DepositServiceImpl implements DepositService {
    private final AccountRepository accountRepository;

    public DepositServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public TransactionResultDTO deposit(TransactionRequestDTO request) {
        if (!request.getAccountNumber().equals(request.getConfirmAccountNumber())) {
            throw new BankingException("Something went wrong: Account numbers do not match");
        }

        Account account = accountRepository.findById(request.getAccountNumber())
                .orElseThrow(() -> new AccountOwnershipException());


        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setAccount(account);

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            transaction.setDescription("Deposit failed: Amount must be greater than 0");
            transaction.setBalanceAfterTransaction(account.getBalance());
            transaction.setStatus(TransactionStatus.FAILED);

            account.getTransactions().add(transaction);
            accountRepository.save(account);

            TransactionResultDTO response = new TransactionResultDTO();
            response.setMessage("Deposit failed: Amount must be greater than 0");
            return response;
        }


        account.deposit(request.getAmount());
        transaction.setDescription("₹" + request.getAmount() + " credited successfully");
        transaction.setBalanceAfterTransaction(account.getBalance());
        transaction.setStatus(TransactionStatus.SUCCESS);

        account.getTransactions().add(transaction);
        accountRepository.save(account);


        TransactionResultDTO response = new TransactionResultDTO();
        response.setMessage("₹" + request.getAmount() + " credited successfully in your account");

        return response;
    }
}
