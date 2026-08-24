package com.nikhil.BankingApplication.service.impl;

import com.nikhil.BankingApplication.dto.TransactionRequestDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.entity.Account;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepositServiceImplTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private DepositServiceImpl depositService;

    private Account account;


    @BeforeEach
    void setup() {
        account = new Account();
        account.setAccountNumber("123456");
        account.setBalance(BigDecimal.valueOf(5000));
        account.setTransactions(new ArrayList<>());
    }

    @Test
    void deposit_accountNumberMismatch() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountNumber("123456");
        request.setConfirmAccountNumber("999999");
        request.setAmount(BigDecimal.valueOf(1000));

        assertThrows(BankingException.class, () -> depositService.deposit(request));
    }

    @Test
    void deposit_accountNotFound() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountNumber("123456");
        request.setConfirmAccountNumber("123456");
        request.setAmount(BigDecimal.valueOf(1000));

        when(accountRepository.findById("123456")).thenReturn(Optional.empty());

        Assertions.assertThrows(AccountOwnershipException.class, () -> depositService.deposit(request));
    }

    @Test
    void deposit_invalidAmount() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountNumber("123456");
        request.setConfirmAccountNumber("123456");
        request.setAmount(BigDecimal.ZERO);

        when(accountRepository.findById("123456")).thenReturn(Optional.of(account));

        TransactionResultDTO result = depositService.deposit(request);

        Assertions.assertEquals(TransactionStatus.FAILED, account.getTransactions().get(0).getStatus());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void deposit_success() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setAccountNumber("123456");
        request.setConfirmAccountNumber("123456");
        request.setAmount(BigDecimal.valueOf(1000));

        when(accountRepository.findById("123456")).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        TransactionResultDTO result = depositService.deposit(request);

        Assertions.assertEquals(BigDecimal.valueOf(6000), account.getBalance());
        Assertions.assertEquals(TransactionStatus.SUCCESS, account.getTransactions().get(0).getStatus());
        Assertions.assertTrue(result.getMessage().contains("credited successfully"));
    }
}
