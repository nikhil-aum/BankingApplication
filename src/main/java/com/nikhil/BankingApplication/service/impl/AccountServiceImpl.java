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
import com.nikhil.BankingApplication.service.AccountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountServiceImpl(AccountRepository accountRepository,CustomerRepository customerRepository){
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public AccountDetailsDTO createAccount(CreateAccountDTO request, String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));


        AccountType type = request.getAccountType();

        boolean exists = accountRepository.existsByOwnerAndAccountType(customer, type);
        if (exists) {
            throw new BankingException("Customer already has a " + type.name() + " account");
        }

        Account account = new Account();
        account.setAccountType(type);
        account.setBalance(BigDecimal.ZERO);
        account.setOwner(customer);
        account.setAccountNumber(generateAccountNumber());

        Account savedAccount = accountRepository.save(account);


        AccountDetailsDTO response = new AccountDetailsDTO();
        response.setAccountNumber(savedAccount.getAccountNumber());
        response.setAccountType(savedAccount.getAccountType().name());
        response.setBalance(savedAccount.getBalance());
        response.setOwner(savedAccount.getOwner().getName());

        return response;
    }

    @Override
    public TransactionResultDTO checkBalance(String accountNumber, String email) {

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new BankingException("Customer not found"));


        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new BankingException("Wrong account number....."));

        if (!account.getOwner().getEmail().equals(email)) {
            throw new AccountOwnershipException();
        }

        TransactionResultDTO response = new TransactionResultDTO();
        response.setMessage("Balance in your account : " + account.getBalance());

        return response;
    }




    private String generateAccountNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}
