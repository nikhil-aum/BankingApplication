package com.nikhil.BankingApplication.service.impl;

import com.nikhil.BankingApplication.dto.AccountRequest;
import com.nikhil.BankingApplication.dto.AccountResponse;
import com.nikhil.BankingApplication.entity.Account;
import com.nikhil.BankingApplication.entity.AccountType;
import com.nikhil.BankingApplication.entity.Customer;
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
    public AccountResponse  createAccount(AccountRequest request, String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (request.getAccountType() == null || request.getAccountType().name().trim().isEmpty()) {
            throw new BankingException("Account type is required and cannot be empty");
        }
        AccountType type = request.getAccountType();

        Account account = new Account();
        account.setAccountType(type);
        account.setBalance(BigDecimal.ZERO);
        account.setOwner(customer);
        account.setAccountNumber(generateAccountNumber());

        Account savedAccount = accountRepository.save(account);


        AccountResponse response = new AccountResponse();
        response.setAccountNumber(savedAccount.getAccountNumber());
        response.setAccountType(savedAccount.getAccountType().name());
        response.setBalance(savedAccount.getBalance());
        response.setOwner(savedAccount.getOwner().getName());

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
