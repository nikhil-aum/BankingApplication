package com.nikhil.BankingApplication.service.impl;

import com.nikhil.BankingApplication.dto.CustomerLoginDTO;
import com.nikhil.BankingApplication.dto.CustomerRegistrationDTO;
import com.nikhil.BankingApplication.entity.Customer;
import com.nikhil.BankingApplication.exception.DuplicateCustomerException;
import com.nikhil.BankingApplication.exception.InvalidCredentialsException;
import com.nikhil.BankingApplication.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Customer customer;

    @BeforeEach
    void setup(){
      customer = new Customer();
      customer.setName("Nikhil");
      customer.setEmail("nikhil@gmail.com");
      customer.setPassword("fgdgwfdrytrtdywbh7672");
    }

    @Test
    void register_success(){
        CustomerRegistrationDTO dto = new CustomerRegistrationDTO();
        dto.setName("Nikhil");
        dto.setPassword("12345");

        when(customerRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("fgdgwfdrytrtdywbh7672");

        authService.register(dto);

        verify(customerRepository,times(1)).save(any(Customer.class));
    }

    @Test
    void register_ShouldThrowDuplicateCustomerException_WhenEmailExists() {
        CustomerRegistrationDTO dto = new CustomerRegistrationDTO();
        dto.setEmail("nikhil@gmail.com");

        when(customerRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(customer));

        Assertions.assertThrows(DuplicateCustomerException.class, () -> authService.register(dto));
    }

    @Test
    void login_success(){
        CustomerLoginDTO dto = new CustomerLoginDTO();
        dto.setEmail("nikhil@gmail.com");
        dto.setPassword("12345");

        when(customerRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(dto.getPassword(),customer.getPassword())).thenReturn(true);
        when(jwtService.generateToken(customer.getEmail())).thenReturn("jwt-token");

        String token = authService.login(dto);
        Assertions.assertEquals("jwt-token",token);
    }

    @Test
    void login_failed_emailNotFound() {
        CustomerLoginDTO dto = new CustomerLoginDTO();
        dto.setEmail("mbxm@gmail.com");
        dto.setPassword("12345");

        when(customerRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(InvalidCredentialsException.class, () -> authService.login(dto));
    }

    @Test
    void login_failed_invalidPassword(){
        CustomerLoginDTO dto = new CustomerLoginDTO();
        dto.setEmail("nikhil@gmail.com");
        dto.setPassword("hjgg73");

        when(customerRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(dto.getPassword(),customer.getPassword())).thenReturn(false);
        Assertions.assertThrows(InvalidCredentialsException.class, () -> authService.login(dto));
    }
}
