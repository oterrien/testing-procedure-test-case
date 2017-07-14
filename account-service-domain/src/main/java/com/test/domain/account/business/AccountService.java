package com.test.domain.account.business;

import com.test.domain.account.api.exception.OverdraftNotAuthorizedException;
import com.test.domain.account.api.model.IAccount;
import com.test.domain.account.api.service.IAccountService;
import com.test.domain.account.spi.IAccountRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class AccountService implements IAccountService {

    private final IAccountRepository accountRepository;

    @Override
    public Optional<IAccount> get(String accountNumber) {
        return accountRepository.find(accountNumber);
    }

    @Override
    public String create(IAccount account) {
        return accountRepository.create(account);
    }

    @Override
    public void makeDeposit(IAccount account, double amount) {

        account.setBalance(account.getBalance() + amount);
        accountRepository.update(account.getNumber(), account);
    }

    @Override
    public void makeWithdrawal(IAccount account, double amount) throws OverdraftNotAuthorizedException {

        double result = account.getBalance() - amount;

        if (result < 0){
            throw new OverdraftNotAuthorizedException(account.getNumber());
        }

        account.setBalance(result);
        accountRepository.update(account.getNumber(), account);
    }
}
