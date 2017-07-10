package com.test.infra.user.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordPayload {

    @JsonProperty
    @NonNull
    @NotEmpty
    private String value;

    @JsonProperty
    private boolean encoded;

    public PasswordPayload(String value) {
        this.value = value;
        this.encoded = false;
    }
}