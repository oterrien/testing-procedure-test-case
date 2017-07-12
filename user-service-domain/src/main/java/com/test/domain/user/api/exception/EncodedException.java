package com.test.domain.user.api.exception;

public class EncodedException extends RuntimeException {
    public EncodedException(Exception e) {
        super(e);
    }
}