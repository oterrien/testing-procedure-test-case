package com.test.infra.user.rest;

import com.test.domain.user.api.EncodedException;
import com.test.domain.user.api.NotAuthorizedException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class UserRestControllerAdvice {

    @ExceptionHandler(EncodedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Error handle(EncodedException e) {
        return new Error(e.getMessage());
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Error handle(NotAuthorizedException e) {
        return new Error(e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Error handle(DataIntegrityViolationException e) {
        return new Error(e.getMessage());
    }

    @ExceptionHandler(UserMapperService.NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Error handle(UserMapperService.NotFoundException e) {
        return new Error("Entity not found");
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Error handle(Throwable e) {
        return new Error(e.getMessage());
    }

    @RequiredArgsConstructor
    public static class Error {

        @Getter
        private final String message;
    }
}
