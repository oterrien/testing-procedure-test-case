package com.test.infra.user.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.test.domain.user.api.IUser;
import com.test.domain.user.api.UserRole;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
public class UserPayload implements IUser {

    @JsonProperty
    private int id;

    @JsonProperty
    @NotNull
    @NotEmpty
    private String login;

    @JsonProperty
    @NotNull
    @NotEmpty
    private String password;

    @JsonProperty
    @NotNull
    private UserRole role;
}

