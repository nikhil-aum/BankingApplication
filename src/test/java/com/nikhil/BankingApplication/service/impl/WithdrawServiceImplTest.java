package com.nikhil.BankingApplication.service.impl;

import com.nikhil.BankingApplication.dto.TransactionRequestDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.entity.Account;
import com.nikhil.BankingApplication.entity.Customer;
import com.nikhil.BankingApplication.entity.TransactionStatus;
import com.nikhil.BankingApplication.exception.AccountOwnershipException;
import com.nikhil.BankingApplication.exception.BankingException;
import com.nikhil.BankingApplication.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WithdrawServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private WithdrawServiceImpl withdrawService;

    private Account account;
    private Customer owner;

    @BeforeEach
    void setup(){
        owner = new Customer();
        owner.setEmail("testuser@example.com");

        account = new Account();
        account.setAccountNumber("123456");
        account.setBalance(BigDecimal.valueOf(5000));
        account.setTransactions(new ArrayList<>());
        account.setOwner(owner);
    }

    @Test
    void withdraw_accountNumberMismatch() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountNumber("123456");
        request.setConfirmAccountNumber("999999");
        request.setAmount(BigDecimal.valueOf(1000));

        Assertions.assertThrows(BankingException.class,
                () -> withdrawService.withdraw(request, "testuser@example.com"));
    }

    @Test
    void withdraw_accountNotFound() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountNumber("123456");
        request.setConfirmAccountNumber("123456");
        request.setAmount(BigDecimal.valueOf(1000));

        when(accountRepository.findById("123456")).thenReturn(Optional.empty());

        Assertions.assertThrows(AccountOwnershipException.class,
                () -> withdrawService.withdraw(request, "testuser@example.com"));
    }

    @Test
    void withdraw_ownershipViolation() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountNumber("123456");
        request.setConfirmAccountNumber("123456");
        request.setAmount(BigDecimal.valueOf(1000));

        when(accountRepository.findById("123456")).thenReturn(Optional.of(account));

        Assertions.assertThrows(AccountOwnershipException.class,
                () -> withdrawService.withdraw(request, "otheruser@example.com"));
    }

    @Test
    void withdraw_invalidAmount() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountNumber("123456");
        request.setConfirmAccountNumber("123456");
        request.setAmount(BigDecimal.ZERO);

        when(accountRepository.findById("123456")).thenReturn(Optional.of(account));

        TransactionResultDTO result = withdrawService.withdraw(request, "testuser@example.com");

        Assertions.assertEquals(TransactionStatus.FAILED, account.getTransactions().get(0).getStatus());
        Assertions.assertTrue(result.getMessage().contains("Withdraw failed"));
    }

    @Test
    void withdraw_insufficientBalance() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountNumber("123456");
        request.setConfirmAccountNumber("123456");
        request.setAmount(BigDecimal.valueOf(10000));

        when(accountRepository.findById("123456")).thenReturn(Optional.of(account));

        Assertions.assertThrows(BankingException.class,
                () -> withdrawService.withdraw(request, "testuser@example.com"));
        Assertions.assertEquals(TransactionStatus.FAILED, account.getTransactions().get(0).getStatus());

    }

    @Test
    void withdraw_success_shouldUpdateBalanceAndTransaction() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountNumber("123456");
        request.setConfirmAccountNumber("123456");
        request.setAmount(BigDecimal.valueOf(1000));

        when(accountRepository.findById("123456")).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        TransactionResultDTO result = withdrawService.withdraw(request, "testuser@example.com");

        Assertions.assertEquals(BigDecimal.valueOf(4000), account.getBalance());
        Assertions.assertEquals(TransactionStatus.SUCCESS, account.getTransactions().get(0).getStatus());
        Assertions.assertTrue(result.getMessage().contains("debited successfully"));
    }
}
