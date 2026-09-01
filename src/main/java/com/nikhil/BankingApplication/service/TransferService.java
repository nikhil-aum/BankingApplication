package com.nikhil.BankingApplication.service;

import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.dto.MoneyTransferDTO;

public interface TransferService {
    TransactionResultDTO transfer(MoneyTransferDTO request,String email);
}
