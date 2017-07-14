package com.test.domain.account.api.service;

import com.test.domain.account.api.exception.InsufficientRoleException;
import com.test.domain.account.api.exception.AccountNotFoundException;
import com.test.domain.account.api.exception.OverdraftNotAuthorizedException;
import com.test.domain.account.api.model.IAccount;

import java.util.Optional;

public interface IAccountService {

    Optional<IAccount> get(String accountNumber) throws InsufficientRoleException;

    String create(IAccount account) throws InsufficientRoleException;

    default void makeDeposit(String accountNumber, double amount) throws InsufficientRoleException, AccountNotFoundException {

        Optional<IAccount> account = get(accountNumber);
        if (!account.isPresent()) {
            throw new AccountNotFoundException(accountNumber);
        }
        makeDeposit(account.get(), amount);
    }

    void makeDeposit(IAccount account, double amount) throws InsufficientRoleException, AccountNotFoundException;

    default void makeWithdrawal(String accountNumber, double amount) throws InsufficientRoleException, AccountNotFoundException, OverdraftNotAuthorizedException {

        Optional<IAccount> account = get(accountNumber);
        if (!account.isPresent()) {
            throw new AccountNotFoundException(accountNumber);
        }
        makeWithdrawal(account.get(), amount);
    }

    void makeWithdrawal(IAccount account, double amount) throws InsufficientRoleException, AccountNotFoundException, OverdraftNotAuthorizedException;
}
