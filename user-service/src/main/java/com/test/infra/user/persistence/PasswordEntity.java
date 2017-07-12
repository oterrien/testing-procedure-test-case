package com.test.infra.user.persistence;

import com.test.domain.user.api.model.IPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordEntity implements IPassword {

    @Column(name = "PASSWORD", nullable=false)
    private String value;

    @Column(name = "IS_ENCODED", nullable=false)
    private Boolean encoded;

    public PasswordEntity(String value) {
        this.value = value;
        this.encoded = false;
    }

    @Override
    public boolean isEncoded() {
        return encoded;
    }

    @Override
    public void setEncoded(boolean isEncoded) {
        this.encoded = isEncoded;
    }
}
