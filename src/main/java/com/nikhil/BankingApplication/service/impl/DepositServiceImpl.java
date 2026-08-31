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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DepositServiceImpl implements DepositService {

    private static final Logger logger = LoggerFactory.getLogger(DepositServiceImpl.class);

    private final AccountRepository accountRepository;

    public DepositServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public TransactionResultDTO deposit(TransactionRequestDTO request,String email) {


        logger.info("Deposit request received for account {} with amount {}",
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

        if (!account.getOwner().getEmail().equals(email)) {
            logger.warn("Ownership violation: User {} tried to deposit in account {}",
                    email, account.getAccountNumber());
            throw new AccountOwnershipException();
        }

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setAccount(account);

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            logger.warn("Deposit failed for account {}. Invalid amount: {}",

             request.getAccountNumber(), request.getAmount());
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

        logger.info("Deposit successful. Account {} new balance: {}", account.getAccountNumber(), account.getBalance());


        TransactionResultDTO response = new TransactionResultDTO();
        response.setMessage("₹" + request.getAmount() + " credited successfully in your account");

        return response;
    }
}
