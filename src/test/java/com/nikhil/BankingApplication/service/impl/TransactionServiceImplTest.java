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
        transaction.setStatus(TransactionStatus.SUCCESS); account.setTransactions(List.of(transaction));
        customer.setAccounts(List.of(account));
    }

    @Test
    void getTransactionHistory_success() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        List<TransactionHistoryDTO> result = transactionService.getTransactionHistory(account.getAccountNumber(), customer.getEmail());

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("DEPOSIT", result.get(0).getType());
    }

    @Test
    void getTransactionHistory_customerNotFound() {
        when(customerRepository.findByEmail("jhjh@gmail.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistory(account.getAccountNumber(), "jhjh@gmail.com"));
    }

    @Test
    void getTransactionHistory_accountNotFound() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById("999999")).thenReturn(Optional.empty());

        Assertions.assertThrows(AccountNotFoundException.class,
                () -> transactionService.getTransactionHistory("999999", customer.getEmail()));
    }

    @Test
    void getTransactionHistoryByTypeDeposit_success() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        List<TransactionHistoryDTO> result = transactionService.getTransactionHistoryByTypeDeposit(account.getAccountNumber(), customer.getEmail());

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("DEPOSIT", result.get(0).getType());
    }

    @Test
    void getTransactionHistoryByTypeDeposit_noDeposits() {
        transaction.setType(TransactionType.WITHDRAW);
        account.setTransactions(List.of(transaction));

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByTypeDeposit(account.getAccountNumber(), customer.getEmail()));
    }

    @Test
    void getWithdrawTransactions_success() {
        transaction.setType(TransactionType.WITHDRAW);
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        List<TransactionHistoryDTO> result =
                transactionService.getTransactionHistoryByTypeWithdraw(account.getAccountNumber(), customer.getEmail());

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("WITHDRAW", result.get(0).getType());
        Assertions.assertEquals(BigDecimal.valueOf(1000), result.get(0).getAmount());
    }

    @Test
    void getWithdrawTransactions_customerNotFound() {
        when(customerRepository.findByEmail("abc@gmail.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByTypeWithdraw(account.getAccountNumber(), "abc@gmail.com"));
    }

    @Test
    void getWithdrawTransactions_accountNotFound() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById("999999")).thenReturn(Optional.empty());

        Assertions.assertThrows(AccountNotFoundException.class,
                () -> transactionService.getTransactionHistoryByTypeWithdraw("999999", customer.getEmail()));
    }

    @Test
    void getWithdrawTransactions_ownershipMismatch() {
        Customer anotherCustomer = new Customer();
        anotherCustomer.setEmail("ram@gmail.com");
        account.setOwner(anotherCustomer);

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        Assertions.assertThrows(AccountOwnershipException.class,
                () -> transactionService.getTransactionHistoryByTypeWithdraw(account.getAccountNumber(), customer.getEmail()));
    }

    @Test
    void getWithdrawTransactions_noWithdrawsFound() {

        transaction.setType(TransactionType.DEPOSIT);
        account.setTransactions(List.of(transaction));

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByTypeWithdraw(account.getAccountNumber(), customer.getEmail()));
    }

    @Test
    void getTransferInTransactions_success() {
        transaction.setType(TransactionType.TRANSFER_IN);
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        List<TransactionHistoryDTO> result =
                transactionService.getTransactionHistoryByTypeTransferIn(account.getAccountNumber(), customer.getEmail());

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("TRANSFER_IN", result.get(0).getType());
        Assertions.assertEquals(BigDecimal.valueOf(1000), result.get(0).getAmount());
    }

    @Test
    void getTransferInTransactions_customerNotFound() {
        when(customerRepository.findByEmail("njj@gmail.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByTypeTransferIn(account.getAccountNumber(), "njj@gmail.com"));
    }

    @Test
    void getTransferInTransactions_accountNotFound() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById("999999")).thenReturn(Optional.empty());

        Assertions.assertThrows(AccountNotFoundException.class,
                () -> transactionService.getTransactionHistoryByTypeTransferIn("999999", customer.getEmail()));
    }

    @Test
    void getTransferInTransactions_ownershipMismatch() {
        Customer anotherCustomer = new Customer();
        anotherCustomer.setEmail("ram@gmail.com");
        account.setOwner(anotherCustomer);

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        Assertions.assertThrows(AccountOwnershipException.class,
                () -> transactionService.getTransactionHistoryByTypeTransferIn(account.getAccountNumber(), customer.getEmail()));
    }

    @Test
    void getTransferInTransactions_noTransferInFound() {
        transaction.setType(TransactionType.WITHDRAW);
        account.setTransactions(List.of(transaction));

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByTypeTransferIn(account.getAccountNumber(), customer.getEmail()));
    }


    @Test
    void getTransferOutTransactions_success() {
        transaction.setType(TransactionType.TRANSFER_OUT);
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        List<TransactionHistoryDTO> result =
                transactionService.getTransactionHistoryByTypeTransferOut(account.getAccountNumber(), customer.getEmail());

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("TRANSFER_OUT", result.get(0).getType());
        Assertions.assertEquals(BigDecimal.valueOf(1000), result.get(0).getAmount());
    }

    @Test
    void getTransferOutTransactions_customerNotFound() {
        when(customerRepository.findByEmail("uhu@gmail.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByTypeTransferOut(account.getAccountNumber(), "uhu@gmail.com"));
    }

    @Test
    void getTransferOutTransactions_accountNotFound() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById("999999")).thenReturn(Optional.empty());

        Assertions.assertThrows(AccountNotFoundException.class,
                () -> transactionService.getTransactionHistoryByTypeTransferOut("999999", customer.getEmail()));
    }

    @Test
    void getTransferOutTransactions_ownershipMismatch() {
        Customer anotherCustomer = new Customer();
        anotherCustomer.setEmail("ram@gmail.com");
        account.setOwner(anotherCustomer);

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        Assertions.assertThrows(AccountOwnershipException.class,
                () -> transactionService.getTransactionHistoryByTypeTransferOut(account.getAccountNumber(), customer.getEmail()));
    }

    @Test
    void getTransferOutTransactions_noTransferOutFound() {
        transaction.setType(TransactionType.DEPOSIT);
        account.setTransactions(List.of(transaction));

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByTypeTransferOut(account.getAccountNumber(), customer.getEmail()));
    }


    @Test
    void getTransactionsByBalance_success() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        List<TransactionHistoryDTO> result =
                transactionService.getTransactionHistoryByBalance(8000, customer.getEmail());

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("DEPOSIT", result.get(0).getType());
        Assertions.assertEquals(BigDecimal.valueOf(1000), result.get(0).getAmount());
    }

    @Test
    void getTransactionsByBalance_invalidAmount() {

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByBalance(0, customer.getEmail()));

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByBalance(-100, customer.getEmail()));
    }

    @Test
    void getTransactionsByBalance_customerNotFound() {
        when(customerRepository.findByEmail("abc@gmail.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByBalance(5000, "abc@gmail.com"));
    }

    @Test
    void getTransactionsByBalance_noTransactionsFound() {

        transaction.setBalanceAfterTransaction(BigDecimal.valueOf(6000));
        account.setTransactions(List.of(transaction));

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByBalance(5000, customer.getEmail()));
    }

    @Test
    void getTransactionsByTime_success() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        List<TransactionHistoryDTO> result =
                transactionService.getTransactionHistoryByTime("10:00", "11:00", customer.getEmail());

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("DEPOSIT", result.get(0).getType());
        Assertions.assertEquals(BigDecimal.valueOf(1000), result.get(0).getAmount());
    }

    @Test
    void getTransactionsByTime_noTransactionsFound() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByTime("12:00", "13:00", customer.getEmail()));
    }

    @Test
    void getTransactionsByTime_customerNotFound() {
        when(customerRepository.findByEmail("abg@gmail.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByTime("10:00", "11:00", "abg@gmail.com"));
    }

    @Test
    void getTransactionsByStatus_success() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        List<TransactionHistoryDTO> result =
                transactionService.getTransactionHistoryByStatus("SUCCESS", customer.getEmail());

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("SUCCESS", result.get(0).getStatus());
    }

    @Test
    void getTransactionsByStatus_invalidStatus() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByStatus("PENDING", customer.getEmail()));
    }

    @Test
    void getTransactionsByStatus_customerNotFound() {
        when(customerRepository.findByEmail("wrong@gmail.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByStatus("SUCCESS", "wrong@gmail.com"));
    }

    @Test
    void getTransactionsByStatus_noTransactionsFound() {
        transaction.setStatus(TransactionStatus.FAILED);
        account.setTransactions(List.of(transaction));
        customer.setAccounts(List.of(account));

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        Assertions.assertThrows(BankingException.class,
                () -> transactionService.getTransactionHistoryByStatus("SUCCESS", customer.getEmail()));
    }






}
