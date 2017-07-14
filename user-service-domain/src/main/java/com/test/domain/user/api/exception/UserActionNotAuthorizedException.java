package com.test.domain.user.api.exception;

public class UserActionNotAuthorizedException extends RuntimeException {
    public UserActionNotAuthorizedException(String message) {
        super(message);
    }
}