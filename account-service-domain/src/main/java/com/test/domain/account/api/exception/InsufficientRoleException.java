package com.test.domain.account.api.exception;

public class InsufficientRoleException extends Exception {

    public InsufficientRoleException(String message) {
        super(message);
    }
}