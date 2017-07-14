package com.test.domain.account.api.exception;

public class OverdraftNotAuthorizedException extends Exception {

    public OverdraftNotAuthorizedException(String accountNumber) {
        super("Overdraft is not authorized for account #" + accountNumber);
    }
}