package com.test.infra.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.test.domain.user.api.IUser;
import com.test.domain.user.api.UserRole;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserPayload implements IUser {

    @JsonProperty
    private int id;

    @JsonProperty
    @NotNull
    private String login;

    @JsonProperty
    @NotNull
    private String password;

    @JsonProperty
    @NotNull
    private UserRole role;
}

