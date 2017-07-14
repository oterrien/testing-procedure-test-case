package com.test.infra.user.rest;

import com.test.domain.user.api.exception.EncodedException;
import com.test.domain.user.api.exception.UserActionNotAuthorizedException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class UserRestControllerAdvice {

    @ExceptionHandler(EncodedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Error handle(EncodedException e) {
        log.error(e.getMessage(), e);
        return new Error(e.getMessage());
    }

    @ExceptionHandler(UserActionNotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Error handle(UserActionNotAuthorizedException e) {
        log.error(e.getMessage(), e);
        return new Error(e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Error handle(DataIntegrityViolationException e) {
        log.error(e.getMessage(), e);
        return new Error(e.getMessage());
    }

    @ExceptionHandler(UserMapperService.NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Error handle(UserMapperService.NotFoundException e) {
        log.error(e.getMessage(), e);
        return new Error("Entity not found");
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Error handle(Throwable e) {
        log.error(e.getMessage(), e);
        return new Error(e.getMessage());
    }

    @RequiredArgsConstructor
    public static class Error {

        @Getter
        private final String message;
    }
}
