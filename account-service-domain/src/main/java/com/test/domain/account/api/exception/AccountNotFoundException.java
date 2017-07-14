package com.test.domain.account.api.exception;

public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(String accountNumber) {
        super("Account #" + accountNumber + " does not exist");
    }
}
