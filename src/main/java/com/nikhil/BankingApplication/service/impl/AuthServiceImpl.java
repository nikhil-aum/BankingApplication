package com.nikhil.BankingApplication.service.impl;

import com.nikhil.BankingApplication.dto.CustomerLoginDTO;
import com.nikhil.BankingApplication.dto.CustomerRegistrationDTO;
import com.nikhil.BankingApplication.entity.Customer;
import com.nikhil.BankingApplication.exception.DuplicateCustomerException;
import com.nikhil.BankingApplication.exception.InvalidCredentialsException;
import com.nikhil.BankingApplication.repository.CustomerRepository;
import com.nikhil.BankingApplication.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final CustomerRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthServiceImpl(CustomerRepository repo,PasswordEncoder encoder,JwtService jwtService){
        this.repo = repo;
        this.encoder=encoder;
        this.jwtService = jwtService;
    }

    @Override
    public void register(CustomerRegistrationDTO request) {
        if (repo.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateCustomerException("Email already registered");
        }
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPassword(encoder.encode(request.getPassword()));
        repo.save(customer);
    }

    @Override
    public String login(CustomerLoginDTO request) {
        Customer customer = repo.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException());
        if (!encoder.matches(request.getPassword(), customer.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return jwtService.generateToken(customer.getEmail());
    }



}
