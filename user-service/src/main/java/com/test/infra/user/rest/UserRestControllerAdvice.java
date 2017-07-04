package com.test.infra.user.rest;

import com.test.domain.user.business.UserServiceWithAuthorization;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserRestControllerAdvice {

    @ExceptionHandler(UserServiceWithAuthorization.NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Error handleNotAuthorizedException(UserServiceWithAuthorization.NotAuthorizedException e) {
        return new Error(e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Error handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new Error(e.getMessage());
    }

    @ExceptionHandler(UserMapperService.NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Error handleNotFoundException(UserMapperService.NotFoundException e) {
        return new Error("Entity not found");
    }

    @RequiredArgsConstructor
    private static class Error {

        @Getter
        private final String message;
    }
}
