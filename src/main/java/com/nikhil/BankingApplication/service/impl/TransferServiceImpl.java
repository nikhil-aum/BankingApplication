package com.nikhil.BankingApplication.service.impl;

import com.nikhil.BankingApplication.dto.TransactionResultDTO;
import com.nikhil.BankingApplication.dto.MoneyTransferDTO;
import com.nikhil.BankingApplication.entity.Account;
import com.nikhil.BankingApplication.entity.Transaction;
import com.nikhil.BankingApplication.entity.TransactionStatus;
import com.nikhil.BankingApplication.entity.TransactionType;
import com.nikhil.BankingApplication.exception.AccountOwnershipException;
import com.nikhil.BankingApplication.exception.BankingException;
import com.nikhil.BankingApplication.repository.AccountRepository;
import com.nikhil.BankingApplication.service.TransferService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferServiceImpl implements TransferService {
    private final AccountRepository accountRepository;

    public TransferServiceImpl(AccountRepository accountRepository){
        this.accountRepository=accountRepository;
    }


    @Override
    public TransactionResultDTO transfer(MoneyTransferDTO request) {
        if (!request.getRecipientAccountNumber().equals(request.getConfirmRecipientAccountNumber())) {
            throw new BankingException("Something went wrong.....");
        }

        Account sender = accountRepository.findById(request.getSenderAccountNumber())
                .orElseThrow(() -> new AccountOwnershipException());

        Account recipient = accountRepository.findById(request.getRecipientAccountNumber())
                .orElseThrow(() -> new BankingException("Recipient account not exist..... " ));

        if (sender.getAccountNumber().equals(recipient.getAccountNumber())) {
            throw new BankingException("Transfer failed: Sender and recipient accounts can't be same");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            Transaction failedTx = new Transaction(
                    null,
                    TransactionType.TRANSFER_OUT,
                    request.getAmount(),
                    null,
                    "Transfer failed: Amount must be greater than 0",
                    sender.getBalance(),
                    TransactionStatus.FAILED,
                    sender
            );
            sender.getTransactions().add(failedTx);
            accountRepository.save(sender);
            throw new BankingException("Amount must be greater than 0");
        }

        if (request.getAmount().compareTo(sender.getBalance()) > 0) {
            Transaction failedTx = new Transaction(
                    null,
                    TransactionType.TRANSFER_OUT,
                    request.getAmount(),
                    null,
                    "Transfer failed: Insufficient balance",
                    sender.getBalance(),
                    TransactionStatus.FAILED,
                    sender
            );
            sender.getTransactions().add(failedTx);
            accountRepository.save(sender);
            throw new BankingException("Insufficient balance");
        }

        sender.withdraw(request.getAmount());
        recipient.deposit(request.getAmount());

        Transaction senderTx = new Transaction(
                null,
                TransactionType.TRANSFER_OUT,
                request.getAmount(),
                null,
                "₹" + request.getAmount() + " transferred to account " + recipient.getAccountNumber(),
                sender.getBalance(),
                TransactionStatus.SUCCESS,
                sender
        );

        Transaction recipientTx = new Transaction(
                null,
                TransactionType.TRANSFER_IN,
                request.getAmount(),
                null,
                "₹" + request.getAmount() + " received from account " + sender.getAccountNumber(),
                recipient.getBalance(),
                TransactionStatus.SUCCESS,
                recipient
        );

        sender.getTransactions().add(senderTx);
        recipient.getTransactions().add(recipientTx);

        accountRepository.save(sender);
        accountRepository.save(recipient);

        TransactionResultDTO response = new TransactionResultDTO();
        response.setMessage("₹" + request.getAmount() + " transferred successfully from " +sender.getOwner().getName()+ " to " +recipient.getOwner().getName());

       return response;
    }
}
