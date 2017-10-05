package com.test.domain.user.api;

public class UserActionNotAuthorizedException extends RuntimeException {

    public UserActionNotAuthorizedException(String message) {
        super(message);
    }
}