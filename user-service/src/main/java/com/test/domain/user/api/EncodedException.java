package com.test.domain.user.api;

public class EncodedException extends RuntimeException {
    public EncodedException(Exception e) {
        super(e);
    }
}