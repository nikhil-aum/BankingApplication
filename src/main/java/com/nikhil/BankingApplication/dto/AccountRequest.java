package com.nikhil.BankingApplication.dto;

import com.nikhil.BankingApplication.entity.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {

    @NotNull(message="Account type is required")
    private AccountType accountType;


}
