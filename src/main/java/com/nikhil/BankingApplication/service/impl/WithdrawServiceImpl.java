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
import com.nikhil.BankingApplication.service.WithdrawService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class WithdrawServiceImpl implements WithdrawService {

    private static final Logger logger = LoggerFactory.getLogger(WithdrawServiceImpl.class);

    private final AccountRepository accountRepository;

    public WithdrawServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public TransactionResultDTO withdraw(TransactionRequestDTO request) {

        logger.info("Withdraw request received for account {} with amount {}",
                request.getAccountNumber(), request.getAmount());

        if (!request.getAccountNumber().equals(request.getConfirmAccountNumber())) {
            logger.error("Account number mismatch: {} & {}",
                    request.getAccountNumber(), request.getConfirmAccountNumber());
            throw new BankingException("Something went wrong: Account numbers do not match");
        }

        Account account = accountRepository.findById(request.getAccountNumber())
                .orElseThrow(() -> {
                    logger.error("Account not found with number {}", request.getAccountNumber());
                    return new AccountOwnershipException();
                });

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setAmount(request.getAmount());
        transaction.setAccount(account);

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            logger.warn("Withdraw failed for account {}. Invalid amount: {}");

            transaction.setDescription("Withdraw failed: Amount must be greater than 0");
            transaction.setBalanceAfterTransaction(account.getBalance());
            transaction.setStatus(TransactionStatus.FAILED);

            account.getTransactions().add(transaction);
            accountRepository.save(account);

            TransactionResultDTO response = new TransactionResultDTO();
            response.setMessage("Withdraw failed: Amount must be greater than 0");
            return response;
        }

        if (request.getAmount().compareTo(account.getBalance()) > 0) {

            logger.warn("Withdrawal failed for account {}. Requested amount {} is greater than balance {}",
                    account.getAccountNumber(), request.getAmount(), account.getBalance());

            transaction.setDescription("Withdrawal failed: Insufficient balance");
            transaction.setBalanceAfterTransaction(account.getBalance());
            transaction.setStatus(TransactionStatus.FAILED);

            account.getTransactions().add(transaction);
            accountRepository.save(account);

            throw new BankingException("Withdrawal failed: Insufficient balance");
        }

        account.withdraw(request.getAmount());
        transaction.setDescription("₹" + request.getAmount() + " debited successfully");
        transaction.setBalanceAfterTransaction(account.getBalance());
        transaction.setStatus(TransactionStatus.SUCCESS);

        account.getTransactions().add(transaction);
        accountRepository.save(account);

        logger.info("Withdraw successful. Account {} new balance: {}", account.getAccountNumber(), account.getBalance());




        TransactionResultDTO response = new TransactionResultDTO();
        response.setMessage("₹" + request.getAmount() + " debited successfully in your account");

        return response;


    }


}
