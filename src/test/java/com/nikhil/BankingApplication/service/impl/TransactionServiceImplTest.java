package com.nikhil.BankingApplication.service.impl;


import com.nikhil.BankingApplication.dto.TransactionHistoryDTO;
import com.nikhil.BankingApplication.entity.*;
import com.nikhil.BankingApplication.exception.AccountNotFoundException;
import com.nikhil.BankingApplication.exception.AccountOwnershipException;
import com.nikhil.BankingApplication.exception.BankingException;
import com.nikhil.BankingApplication.repository.AccountRepository;
import com.nikhil.BankingApplication.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Customer customer;
    private Account account;
    private Transaction transaction;

    @BeforeEach
    void setup() {
        customer = new Customer();
        customer.setName("Nikhil");
        customer.setEmail("nikhil@gmail.com");

        account = new Account();
        account.setAccountNumber("123456789012");
        account.setOwner(customer);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(BigDecimal.valueOf(1000));
        transaction.setTimestamp(LocalDateTime.of(2026, 8, 27, 10, 30));
        transaction.setDescription("Deposit successful");
        transaction.setBalanceAfterTransaction(BigDecimal.valueOf(6000));
        transaction.setStatus(TransactionStatus.SUCCESS);

        account.setTransactions(List.of(transaction));
        customer.setAccounts(List.of(account));
    }

    @Test
    void getTransactionHistory_success() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        List<TransactionHistoryDTO> result = transactionService.getTransactionHistory(
                account.getAccountNumber(), customer.getEmail(),
                null, null, null, null, null);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("DEPOSIT", result.get(0).getType());
    }

    @Test
    void getTransactionHistory_filterByType_success() {
        transaction.setType(TransactionType.WITHDRAW);
        account.setTransactions(List.of(transaction));

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        List<TransactionHistoryDTO> result = transactionService.getTransactionHistory(
                account.getAccountNumber(), customer.getEmail(),
                "WITHDRAW", null, null, null, null);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("WITHDRAW", result.get(0).getType());
    }

    @Test
    void getTransactionHistory_filterByStatus_success() {
        transaction.setStatus(TransactionStatus.FAILED);
        account.setTransactions(List.of(transaction));

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        List<TransactionHistoryDTO> result = transactionService.getTransactionHistory(
                account.getAccountNumber(), customer.getEmail(),
                null, "FAILED", null, null, null);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("FAILED", result.get(0).getStatus());
    }

    @Test
    void getTransactionHistory_filterByBalance_success() {
        transaction.setBalanceAfterTransaction(BigDecimal.valueOf(4000));
        account.setTransactions(List.of(transaction));

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        List<TransactionHistoryDTO> result = transactionService.getTransactionHistory(
                account.getAccountNumber(), customer.getEmail(),
                null, null, 5000.0, null, null);

        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.get(0).getBalanceAfterTransaction().doubleValue() < 5000);
    }

    @Test
    void getTransactionHistory_filterByTime_success() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        List<TransactionHistoryDTO> result = transactionService.getTransactionHistory(
                account.getAccountNumber(), customer.getEmail(),
                null, null, null, "10:00", "11:00");

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("DEPOSIT", result.get(0).getType());
    }

    @Test
    void getTransactionHistory_customerNotFound() {
        when(customerRepository.findByEmail("wrong@gmail.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistory(account.getAccountNumber(), "wrong@gmail.com",
                        null, null, null, null, null));
    }

    @Test
    void getTransactionHistory_accountNotFound() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById("999999")).thenReturn(Optional.empty());

        Assertions.assertThrows(AccountNotFoundException.class,
                () -> transactionService.getTransactionHistory("999999", customer.getEmail(),
                        null, null, null, null, null));
    }

    @Test
    void getTransactionHistory_ownershipMismatch() {
        Customer anotherCustomer = new Customer();
        anotherCustomer.setEmail("ram@gmail.com");
        account.setOwner(anotherCustomer);

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        Assertions.assertThrows(AccountOwnershipException.class,
                () -> transactionService.getTransactionHistory(account.getAccountNumber(), customer.getEmail(),
                        null, null, null, null, null));
    }
}
