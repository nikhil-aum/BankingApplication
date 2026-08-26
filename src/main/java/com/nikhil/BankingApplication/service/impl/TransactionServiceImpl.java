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
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    @Override
    public List<TransactionHistoryDTO> getTransactionHistoryByTypeDeposit(String accountNumber, String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new BankingException("Customer not found"));

        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Wrong account number: " + accountNumber));

        if (!account.getOwner().getEmail().equals(email)) {
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
            throw new BankingException("No deposit transaction found");
        }

        return deposits;
    }

    @Override
    public List<TransactionHistoryDTO> getTransactionHistoryByTypeWithdraw(String accountNumber, String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new BankingException("Customer not found"));

        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Wrong account number: " + accountNumber));

        if (!account.getOwner().getEmail().equals(email)) {
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
            throw new BankingException("No withdraw transaction found");
        }

        return withdraws;
    }

    @Override
    public List<TransactionHistoryDTO> getTransactionHistoryByTypeTransferIn(String accountNumber, String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new BankingException("Customer not found"));

        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Wrong account number: " + accountNumber));

        if (!account.getOwner().getEmail().equals(email)) {
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
            throw new BankingException("No Transfer-In transaction found");
        }

        return transferIn;
    }

    @Override
    public List<TransactionHistoryDTO> getTransactionHistoryByTypeTransferOut(String accountNumber, String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new BankingException("Customer not found"));

        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Wrong account number: " + accountNumber));

        if (!account.getOwner().getEmail().equals(email)) {
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
            throw new BankingException("No withdraw transaction found");
        }

        return transferOut;
    }

    @Override
    public List<TransactionHistoryDTO> getTransactionHistoryByBalance(double amount, String email) {
        if (amount <= 0) {
            throw new BankingException("Invalid amount.Amount must be greater than 0");
        }

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new BankingException("Customer not found"));


        List<TransactionHistoryDTO> transactions = customer.getAccounts().stream()
                .flatMap(acc->acc.getTransactions().stream())
                .filter(tx->tx.getBalanceAfterTransaction().doubleValue()<amount)
                .map(tx->new TransactionHistoryDTO(
                        tx.getId(),
                        tx.getType().name(),
                        tx.getAmount(),
                        tx.getTimestamp(),
                        tx.getDescription(),
                        tx.getBalanceAfterTransaction(),
                        tx.getStatus().name()
                )).toList();

        if (transactions.isEmpty()) {
            throw new BankingException("No Transaction Found with balance less than " + amount);
        }

        return transactions;
    }

    @Override
    public List<TransactionHistoryDTO> getTransactionHistoryByTime(String from, String to, String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new BankingException("Customer not found"));

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime fromTime = LocalTime.parse(from, timeFmt);
        LocalTime toTime = LocalTime.parse(to, timeFmt);

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
            throw new BankingException("No Transaction Found in given time range");
        }

        return transactions;
    }

    @Override
    public List<TransactionHistoryDTO> getTransactionHistoryByStatus(String status, String email) {

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new BankingException("Customer not found"));

        TransactionStatus txnStatus;
        try {
            txnStatus = TransactionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
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
            throw new BankingException("No Transaction Found with status " + status);
        }

        return transactions;
    }

}
