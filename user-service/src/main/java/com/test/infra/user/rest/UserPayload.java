package com.test.infra.user.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

import static com.test.domain.user.spi.IUser.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPayload {

    @JsonProperty
    private long id;

    @JsonProperty
    @NotNull
    @NotEmpty
    private String login;

    @JsonProperty
    @NotNull
    private PasswordPayload password;

    @JsonProperty
    @NotEmpty
    private Set<Role> roles = new HashSet<>();
}

