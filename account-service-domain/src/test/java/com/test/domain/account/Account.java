package com.test.domain.account;

import com.test.domain.account.api.model.IAccount;
import com.test.domain.user.api.model.IUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Account implements IAccount, Cloneable {

    private String number;
    private final IUser owner;
    private double balance = 0;

    @Override
    protected IAccount clone() {
        try {
            return (IAccount) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
