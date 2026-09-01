package com.nikhil.BankingApplication.service.impl;

import com.nikhil.BankingApplication.dto.MoneyTransferDTO;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransferServiceImpl transferService;

    private Account sender;
    private Account recipient;

    @BeforeEach
    void setup() {
        sender = new Account();
        sender.setAccountNumber("111");
        sender.setBalance(BigDecimal.valueOf(5000));
        sender.setTransactions(new ArrayList<>());
        sender.setOwner(new Customer(null, "ram", "ram@gmail.com", "pass123", null, new ArrayList<>()));

        recipient = new Account();
        recipient.setAccountNumber("222");
        recipient.setBalance(BigDecimal.valueOf(2000));
        recipient.setTransactions(new ArrayList<>());
        recipient.setOwner(new Customer(null, "shyam", "shyam@gmail.com", "pass098", null, new ArrayList<>()));
    }

    @Test
    void transfer_recipientMismatch() {
        MoneyTransferDTO request = new MoneyTransferDTO("111", "222", "999", BigDecimal.valueOf(1000));
        assertThrows(BankingException.class, () -> transferService.transfer(request, "ram@gmail.com"));
    }

    @Test
    void transfer_senderNotFound() {
        MoneyTransferDTO request = new MoneyTransferDTO("111", "222", "222", BigDecimal.valueOf(1000));
        when(accountRepository.findById("111")).thenReturn(Optional.empty());
        assertThrows(AccountOwnershipException.class, () -> transferService.transfer(request, "ram@gmail.com"));
    }

    @Test
    void transfer_recipientNotFound_shouldThrowException() {
        MoneyTransferDTO request = new MoneyTransferDTO("111", "222", "222", BigDecimal.valueOf(1000));
        when(accountRepository.findById("111")).thenReturn(Optional.of(sender));
        when(accountRepository.findById("222")).thenReturn(Optional.empty());
        assertThrows(BankingException.class, () -> transferService.transfer(request, "ram@gmail.com"));
    }

    @Test
    void transfer_sameAccount() {
        MoneyTransferDTO request = new MoneyTransferDTO("111", "111", "111", BigDecimal.valueOf(1000));
        when(accountRepository.findById("111")).thenReturn(Optional.of(sender));
        assertThrows(BankingException.class, () -> transferService.transfer(request, "ram@gmail.com"));
    }

    @Test
    void transfer_invalidAmount() {
        MoneyTransferDTO request = new MoneyTransferDTO("111", "222", "222", BigDecimal.valueOf(-180));
        when(accountRepository.findById("111")).thenReturn(Optional.of(sender));
        when(accountRepository.findById("222")).thenReturn(Optional.of(recipient));
        assertThrows(BankingException.class, () -> transferService.transfer(request, "ram@gmail.com"));
        Assertions.assertEquals(TransactionStatus.FAILED, sender.getTransactions().get(0).getStatus());
    }

    @Test
    void transfer_insufficientBalance(){
        MoneyTransferDTO request = new MoneyTransferDTO("111", "222", "222", BigDecimal.valueOf(6000));
        when(accountRepository.findById("111")).thenReturn(Optional.of(sender));
        when(accountRepository.findById("222")).thenReturn(Optional.of(recipient));
        assertThrows(BankingException.class, () -> transferService.transfer(request, "ram@gmail.com"));
        Assertions.assertEquals(TransactionStatus.FAILED, sender.getTransactions().get(0).getStatus());
    }

    @Test
    void transfer_success_shouldUpdateBalancesAndTransactions() {
        MoneyTransferDTO request = new MoneyTransferDTO("111", "222", "222", BigDecimal.valueOf(1000));
        when(accountRepository.findById("111")).thenReturn(Optional.of(sender));
        when(accountRepository.findById("222")).thenReturn(Optional.of(recipient));
        when(accountRepository.save(sender)).thenReturn(sender);
        when(accountRepository.save(recipient)).thenReturn(recipient);

        TransactionResultDTO result = transferService.transfer(request, "ram@gmail.com");

        Assertions.assertEquals(BigDecimal.valueOf(4000), sender.getBalance());
        Assertions.assertEquals(BigDecimal.valueOf(3000), recipient.getBalance());
        Assertions.assertEquals(TransactionStatus.SUCCESS, sender.getTransactions().get(0).getStatus());
        Assertions.assertEquals(TransactionStatus.SUCCESS, recipient.getTransactions().get(0).getStatus());
        Assertions.assertTrue(result.getMessage().contains("transferred successfully"));
    }
}
