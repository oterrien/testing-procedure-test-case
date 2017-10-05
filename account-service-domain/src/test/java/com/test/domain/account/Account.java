package com.test.domain.account;

import com.test.domain.account.api.model.IAccount;
import com.test.domain.user.spi.IUser;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Account implements IAccount, Cloneable {

    private final IUser owner;
    private String number;
    private double balance = 0;
    private double agreedOverdraft = 0;

    @Override
    protected IAccount clone() {
        try {
            return (IAccount) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
