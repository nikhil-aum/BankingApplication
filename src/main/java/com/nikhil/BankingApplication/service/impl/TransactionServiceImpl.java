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
    public List<TransactionHistoryDTO> getTransactionHistory(String accountNumber,String email){

        logger.info("Fetching transaction history for account {} and customer {}", accountNumber, email);

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Customer not found with email {}", email);
                    return new BankingException("Customer not found");
                });

        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> {
                    logger.error("Account not found: {}", accountNumber);
                    return new AccountNotFoundException("Wrong account number: " + accountNumber);
                });

        if (!account.getOwner().getEmail().equals(email)) {
            logger.warn("Ownership mismatch for account {} and customer {}", accountNumber, email);
            throw new AccountOwnershipException();
        }

        logger.info("Found {} transactions for account {}", account.getTransactions().size(), accountNumber);

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

    @Override
    public List<TransactionHistoryDTO> getTransactionHistoryByTypeDeposit(String accountNumber, String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Customer not found with email {}", email);
                    return new BankingException("Customer not found");
                });

        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> {
                    logger.error("Account not found: {}", accountNumber);
                    return new AccountNotFoundException("Wrong account number: " + accountNumber);
                });

        if (!account.getOwner().getEmail().equals(email)) {
            logger.warn("Ownership mismatch for account {} and customer {}", accountNumber, email);
            throw new AccountOwnershipException();
        }

        List<TransactionHistoryDTO> deposits = account.getTransactions().stream()
                .filter(tx -> tx.getType() == TransactionType.DEPOSIT)
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

        if (deposits.isEmpty()) {
            logger.warn("No deposit transactions found for account {}", accountNumber);
            throw new BankingException("No deposit transaction found");
        }

        logger.info("Found {} deposit transactions for account {}", deposits.size(), accountNumber);
        return deposits;
    }

    @Override
    public List<TransactionHistoryDTO> getTransactionHistoryByTypeWithdraw(String accountNumber, String email) {
        logger.info("Fetching withdraw transactions for account {} and customer {}", accountNumber, email);

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Customer not found with email {}", email);
                    return new BankingException("Customer not found");
                });

        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> {
                    logger.error("Account not found: {}", accountNumber);
                    return new AccountNotFoundException("Wrong account number: " + accountNumber);
                });

        if (!account.getOwner().getEmail().equals(email)) {
            logger.warn("Ownership mismatch for account {} and customer {}", accountNumber, email);
            throw new AccountOwnershipException();
        }

        List<TransactionHistoryDTO> withdraws = account.getTransactions().stream()
                .filter(tx -> tx.getType() == TransactionType.WITHDRAW)
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

        if (withdraws.isEmpty()) {
            logger.warn("No withdraw transactions found for account {}", accountNumber);
            throw new BankingException("No withdraw transaction found");
        }

        logger.info("Found {} withdraw transactions for account {}", withdraws.size(), accountNumber);
        return withdraws;
    }


    @Override
    public List<TransactionHistoryDTO> getTransactionHistoryByTypeTransferIn(String accountNumber, String email) {
        logger.info("Fetching Transfer-In transactions for account {} and customer {}", accountNumber, email);

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Customer not found with email {}", email);
                    return new BankingException("Customer not found");
                });

        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> {
                    logger.error("Account not found: {}", accountNumber);
                    return new AccountNotFoundException("Wrong account number: " + accountNumber);
                });

        if (!account.getOwner().getEmail().equals(email)) {
            logger.warn("Ownership mismatch for account {} and customer {}", accountNumber, email);
            throw new AccountOwnershipException();
        }

        List<TransactionHistoryDTO> transferIn = account.getTransactions().stream()
                .filter(tx -> tx.getType() == TransactionType.TRANSFER_IN)
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

        if (transferIn.isEmpty()) {
            logger.warn("No Transfer-In transactions found for account {}", accountNumber);
            throw new BankingException("No Transfer-In transaction found");
        }

        logger.info("Found {} Transfer-In transactions for account {}", transferIn.size(), accountNumber);
        return transferIn;
    }

    @Override
    public List<TransactionHistoryDTO> getTransactionHistoryByTypeTransferOut(String accountNumber, String email) {
        logger.info("Fetching Transfer-Out transactions for account {} and customer {}", accountNumber, email);

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Customer not found with email {}", email);
                    return new BankingException("Customer not found");
                });

        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> {
                    logger.error("Account not found: {}", accountNumber);
                    return new AccountNotFoundException("Wrong account number: " + accountNumber);
                });

        if (!account.getOwner().getEmail().equals(email)) {
            logger.warn("Ownership mismatch for account {} and customer {}", accountNumber, email);
            throw new AccountOwnershipException();
        }

        List<TransactionHistoryDTO> transferOut = account.getTransactions().stream()
                .filter(tx -> tx.getType() == TransactionType.TRANSFER_OUT)
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

        if (transferOut.isEmpty()) {
            logger.warn("No Transfer-Out transactions found for account {}", accountNumber);
            throw new BankingException("No Transfer-Out transaction found");
        }

        logger.info("Found {} Transfer-Out transactions for account {}", transferOut.size(), accountNumber);
        return transferOut;
    }

    @Override
    public List<TransactionHistoryDTO> getTransactionHistoryByBalance(double amount, String email) {
        logger.info("Fetching transactions by balance less than {} for customer {}", amount, email);

        if (amount <= 0) {
            logger.error("Invalid amount {} provided by customer {}", amount, email);
            throw new BankingException("Invalid amount. Amount must be greater than 0");
        }

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Customer not found with email {}", email);
                    return new BankingException("Customer not found");
                });

        List<TransactionHistoryDTO> transactions = customer.getAccounts().stream()
                .flatMap(acc -> acc.getTransactions().stream())
                .filter(tx -> tx.getBalanceAfterTransaction().doubleValue() < amount)
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

        if (transactions.isEmpty()) {
            logger.warn("No transactions found with balance less than {} for customer {}", amount, email);
            throw new BankingException("No Transaction Found with balance less than " + amount);
        }

        logger.info("Found {} transactions with balance less than {} for customer {}", transactions.size(), amount, email);
        return transactions;
    }

    @Override
    public List<TransactionHistoryDTO> getTransactionHistoryByTime(String from, String to, String email) {
        logger.info("Fetching transactions between {} and {} for customer {}", from, to, email);

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Customer not found with email {}", email);
                    return new BankingException("Customer not found");
                });

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime fromTime = LocalTime.parse(from, timeFmt);
        LocalTime toTime = LocalTime.parse(to, timeFmt);

        logger.debug("Parsed time range: from={} to={}", fromTime, toTime);

        List<TransactionHistoryDTO> transactions = customer.getAccounts().stream()
                .flatMap(acc -> acc.getTransactions().stream())
                .filter(tx -> {
                    LocalTime txnTime = tx.getTimestamp().toLocalTime();
                    return !txnTime.isBefore(fromTime) && !txnTime.isAfter(toTime);
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

        if (transactions.isEmpty()) {
            logger.warn("No transactions found for customer {} in time range {} - {}", email, from, to);
            throw new BankingException("No Transaction Found in given time range");
        }

        logger.info("Found {} transactions for customer {} in time range {} - {}", transactions.size(), email, from, to);
        return transactions;
    }


    @Override
    public List<TransactionHistoryDTO> getTransactionHistoryByStatus(String status, String email) {
        logger.info("Fetching transactions with status {} for customer {}", status, email);

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Customer not found with email {}", email);
                    return new BankingException("Customer not found");
                });

        TransactionStatus txnStatus;
        try {
            txnStatus = TransactionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid status {} provided by customer {}. Allowed values: SUCCESS, FAILED", status, email);
            throw new BankingException("Invalid status. Allowed values: SUCCESS, FAILED");
        }

        List<TransactionHistoryDTO> transactions = customer.getAccounts().stream()
                .flatMap(acc -> acc.getTransactions().stream())
                .filter(tx -> tx.getStatus() == txnStatus)
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

        if (transactions.isEmpty()) {
            logger.warn("No transactions found with status {} for customer {}", status, email);
            throw new BankingException("No Transaction Found with status " + status);
        }

        logger.info("Found {} transactions with status {} for customer {}", transactions.size(), status, email);
        return transactions;
    }


}
