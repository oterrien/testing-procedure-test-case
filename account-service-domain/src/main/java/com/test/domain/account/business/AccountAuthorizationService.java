package com.test.domain.account.business;

import com.test.domain.account.api.exception.InsufficientRoleException;
import com.test.domain.account.api.exception.OverdraftNotAuthorizedException;
import com.test.domain.account.api.model.IAccount;
import com.test.domain.account.api.service.IAccountService;
import com.test.domain.user.spi.IUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static com.test.domain.user.spi.IUser.Role;

@RequiredArgsConstructor
@Slf4j
public class AccountAuthorizationService implements IAccountService {

    private final IAccountService accountService;
    private final IUser currentUser;

    @Override
    public Optional<IAccount> get(String accountNumber) throws InsufficientRoleException {

        Optional<IAccount> account = accountService.get(accountNumber);

        // If I am not ADVISOR and I am not the owner of account --> not authorized
        if (!currentUser.hasRole(Role.ADVISOR) && !account.filter(acc -> acc.getOwner().compareTo(currentUser) == 0).isPresent()) {
            throw new InsufficientRoleException("User " + currentUser.getLogin() + " is not authorized to read account #" + accountNumber);
        }

        return account;
    }

    @Override
    public String create(IAccount account) throws InsufficientRoleException {

        if (!currentUser.hasRole(Role.ADVISOR)) {
            throw new InsufficientRoleException("User " + currentUser.getLogin() + " is not authorized to create account");
        }

        return accountService.create(account);
    }

    @Override
    public void makeDeposit(IAccount account, double amount) throws InsufficientRoleException {

        if (!currentUser.hasRole(Role.ADVISOR) && !Optional.of(account).filter(acc -> acc.getOwner().compareTo(currentUser) == 0).isPresent()) {
            throw new InsufficientRoleException("User " + currentUser.getLogin() + " is not authorized to retrieve account #" + account.getNumber());
        }

        accountService.makeDeposit(account, amount);
    }

    @Override
    public void makeWithdrawal(IAccount account, double amount) throws InsufficientRoleException, OverdraftNotAuthorizedException {

        if (!currentUser.hasRole(Role.ADVISOR) && !Optional.of(account).filter(acc -> acc.getOwner().compareTo(currentUser) == 0).isPresent()) {
            throw new InsufficientRoleException("User " + currentUser.getLogin() + " is not authorized to retrieve account #" + account.getNumber());
        }

        accountService.makeWithdrawal(account, amount);
    }

    @Override
    public void setAgreedOverdraft(IAccount account, double agreedOverdraft) throws InsufficientRoleException {

        if (!currentUser.hasRole(Role.ADVISOR) && !Optional.of(account).filter(acc -> acc.getOwner().compareTo(currentUser) == 0).isPresent()) {
            throw new InsufficientRoleException("User " + currentUser.getLogin() + " is not authorized to retrieve account #" + account.getNumber());
        }

        accountService.setAgreedOverdraft(account, agreedOverdraft);

    }
}
