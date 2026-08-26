package com.nikhil.BankingApplication.service.impl;

import com.nikhil.BankingApplication.dto.CustomerLoginDTO;
import com.nikhil.BankingApplication.dto.CustomerRegistrationDTO;
import com.nikhil.BankingApplication.entity.Customer;
import com.nikhil.BankingApplication.exception.DuplicateCustomerException;
import com.nikhil.BankingApplication.exception.InvalidCredentialsException;
import com.nikhil.BankingApplication.repository.CustomerRepository;
import com.nikhil.BankingApplication.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

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

        logger.info("Registering new customer with email {}", request.getEmail());

        if (repo.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Duplicate registration attempt for email {}", request.getEmail());
            throw new DuplicateCustomerException("Email already registered");
        }
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPassword(encoder.encode(request.getPassword()));
        repo.save(customer);

        logger.info("Customer {} registered successfully", request.getEmail());
    }

    @Override
    public String login(CustomerLoginDTO request) {

        logger.info("Login attempt for email {}", request.getEmail());

        Customer customer = repo.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException());
        if (!encoder.matches(request.getPassword(), customer.getPassword())) {
            logger.error("Login failed. Invalid password for email {}", request.getEmail());
            throw new InvalidCredentialsException();
        }
        String token = jwtService.generateToken(customer.getEmail());
        logger.info("Login successful for email {}. JWT generated.", request.getEmail());

        return token;
    }



}
