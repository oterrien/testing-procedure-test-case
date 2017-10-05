package com.test.domain.account.api.model;

import com.test.domain.user.spi.IUser;

public interface IAccount {

    String getNumber();

    IUser getOwner();

    double getBalance();

    void setBalance(double balance);

    double getAgreedOverdraft();

    void setAgreedOverdraft(double agreedOverdraft);
}
