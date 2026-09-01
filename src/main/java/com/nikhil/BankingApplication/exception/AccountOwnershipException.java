package com.nikhil.BankingApplication.exception;

public class AccountOwnershipException extends RuntimeException {
    public AccountOwnershipException() {
        super("This account doesn't belong to you...." );
    }
}
