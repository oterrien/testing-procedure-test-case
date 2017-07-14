package com.test.domain.account.api.model;

import com.test.domain.user.api.model.IUser;

import java.util.UUID;

public interface IAccount {

    String getNumber();

    IUser getOwner();

    double getBalance();

    void setBalance(double balance);
}
