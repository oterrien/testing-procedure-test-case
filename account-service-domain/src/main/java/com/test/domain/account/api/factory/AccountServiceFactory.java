package com.test.domain.account.api.factory;

import com.test.domain.account.api.service.IAccountService;
import com.test.domain.account.business.AccountAuthorizationService;
import com.test.domain.account.business.AccountService;
import com.test.domain.account.spi.IAccountRepository;
import com.test.domain.user.api.model.IUser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountServiceFactory {

    @Getter
    public static final AccountServiceFactory Instance = new AccountServiceFactory();

    public IAccountService create(IAccountRepository accountRepository, IUser user){
        return new AccountAuthorizationService(new AccountService(accountRepository), user);
    }
}
