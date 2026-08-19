package com.nikhil.BankingApplication.service;

import com.nikhil.BankingApplication.dto.DepositWithdrawRequestRequest;
import com.nikhil.BankingApplication.dto.DepositWithdrawResponse;

public interface DepositService {
    DepositWithdrawResponse deposit(DepositWithdrawRequestRequest request);
}
