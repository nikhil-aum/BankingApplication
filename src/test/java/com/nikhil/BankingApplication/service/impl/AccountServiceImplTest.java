package com.nikhil.BankingApplication.service.impl;


import com.nikhil.BankingApplication.dto.AccountDetailsDTO;
import com.nikhil.BankingApplication.dto.CreateAccountDTO;
import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.entity.Account;
import com.nikhil.BankingApplication.entity.AccountType;
import com.nikhil.BankingApplication.entity.Customer;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Customer customer;
    private Account account;

    @BeforeEach
    void setup(){
        customer = new Customer();
        customer.setName("Nikhil");
        customer.setEmail("nikhil@gmail.com");

        account = new Account();
        account.setAccountNumber("123456789012");
        account.setAccountType(AccountType.SAVING);
        account.setBalance(BigDecimal.valueOf(5000));
        account.setOwner(customer);
    }

    @Test
    void create_accountSuccess(){
        CreateAccountDTO dto = new CreateAccountDTO();
        dto.setAccountType(AccountType.SAVING);

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.existsByOwnerAndAccountType(customer, AccountType.SAVING)).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountDetailsDTO response = accountService.createAccount(dto,customer.getEmail());
        Assertions.assertEquals("123456789012",response.getAccountNumber());
        Assertions.assertEquals("SAVING",response.getAccountType());
        Assertions.assertEquals(BigDecimal.valueOf(5000),response.getBalance());
    }

    @Test
    void createAccount_failed(){
        CreateAccountDTO dto = new CreateAccountDTO();
        dto.setAccountType(AccountType.SAVING);

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.existsByOwnerAndAccountType(customer,AccountType.SAVING)).thenReturn(true);

        Assertions.assertThrows(BankingException.class,()->accountService.createAccount(dto,customer.getEmail()));
    }

    @Test
    void checkBalance_success(){
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        TransactionResultDTO response  = accountService.checkBalance(account.getAccountNumber(),customer.getEmail());
        Assertions.assertTrue(response.getMessage().contains("Balance in your Account"));
    }

    @Test
    void checkBalance_wrongAccountNumber() {
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById("999999")).thenReturn(Optional.empty());

        Assertions.assertThrows(BankingException.class, () -> accountService.checkBalance("999999", customer.getEmail()));
    }

    @Test
    void checkBalance_accountOwnershipMismatch() {
        Customer anotherCustomer = new Customer();
        anotherCustomer.setEmail("ram@gmail.com");
        account.setOwner(anotherCustomer);

        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(accountRepository.findById(account.getAccountNumber())).thenReturn(Optional.of(account));

        Assertions.assertThrows(AccountOwnershipException.class, () -> accountService.checkBalance(account.getAccountNumber(), customer.getEmail()));
    }
}
