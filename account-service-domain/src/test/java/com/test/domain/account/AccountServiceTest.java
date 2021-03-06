package com.test.domain.account;

import com.test.domain.account.api.exception.InsufficientRoleException;
import com.test.domain.account.api.exception.OverdraftNotAuthorizedException;
import com.test.domain.account.api.factory.AccountServiceFactory;
import com.test.domain.account.api.model.IAccount;
import com.test.domain.account.api.service.IAccountService;
import com.test.domain.account.business.AccountService;
import com.test.domain.user.User;
import com.test.domain.user.spi.IUser;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Optional;

import static com.test.domain.user.spi.IUser.Role;

public class AccountServiceTest {

    private final AccountRepositoryMock accountRepositoryMock = new AccountRepositoryMock();
    private final IUser advisor = new User("advisor", "password", Role.ADVISOR);

    //region User Story 1 : creating account
    @Test
    public void anAccountShouldBeCreatedForAGivenClient() throws Exception {

        IUser client = new User("client", "password", Role.CLIENT);
        Account account = new Account(client);

        IAccountService accountService = new AccountService(accountRepositoryMock);
        String accountNumber = accountService.create(account);

        Assertions.assertThat(accountNumber).isNotNull();
        Assertions.assertThat(accountNumber).isNotEmpty();
    }

    @Test
    public void anAdvisorShouldBeAbleToCreateAccountForAGivenClient() throws Exception {

        IUser client = new User("client", "password", Role.CLIENT);
        Account account = new Account(client);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, advisor);
        String accountNumber = accountService.create(account);

        Assertions.assertThat(accountNumber).isNotNull();
        Assertions.assertThat(accountNumber).isNotEmpty();
    }

    @Test(expected = InsufficientRoleException.class)
    public void anNonAdvisorShouldNotBeAbleToCreateAccountForAGivenClient() throws Exception {

        IUser hacker = new User("hacker", "password", Role.CLIENT);
        IUser client = new User("client", "password", Role.CLIENT);
        Account account = new Account(client);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, hacker);
        accountService.create(account);

        Assertions.fail("Exception should be raised");
    }
    //endregion

    //region User Story 2 : reading account
    @Test
    public void anAdvisorShouldBeAbleToReadAnyClientAccount() throws Exception {

        IUser client = new User("client", "password", Role.CLIENT);
        Account account = new Account(client);
        String accountNumber = accountRepositoryMock.create(account);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, advisor);
        Optional<IAccount> accountOptional = accountService.get(accountNumber);

        Assertions.assertThat(accountOptional).isPresent();
        Assertions.assertThat(accountOptional.get().getOwner()).isEqualTo(client);
        Assertions.assertThat(accountOptional.get().getBalance()).isEqualTo(0);
    }

    @Test
    public void aNonAdvisorShouldAbleToReadHisOwnAccount() throws Exception {

        IUser client = new User("client", "password", Role.CLIENT);
        Account account = new Account(client);
        String accountNumber = accountRepositoryMock.create(account);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, client);
        Optional<IAccount> accountOptional = accountService.get(accountNumber);

        Assertions.assertThat(accountOptional).isPresent();
        Assertions.assertThat(accountOptional.get().getOwner()).isEqualTo(client);
        Assertions.assertThat(accountOptional.get().getBalance()).isEqualTo(0);
    }

    @Test(expected = InsufficientRoleException.class)
    public void aNonAdvisorShouldNotBeAbleToReadAccountOfAnotherClient() throws Exception {

        IUser client1 = new User("client1", "password", Role.CLIENT);

        IUser client2 = new User("client2", "password", Role.CLIENT);
        Account account2 = new Account(client2);
        String accountNumber2 = accountRepositoryMock.create(account2);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, client1);
        accountService.get(accountNumber2);

        Assertions.fail("Exception should be raised");
    }
    //endregion

    //region User Story 3 : deposit
    @Test
    public void aClientShouldBeAbleToMakeADepositOnHisAccount() throws Exception {

        IUser client = new User("client", "password", Role.CLIENT);
        Account account = new Account(client);
        account.setBalance(1000);
        String accountNumber = accountRepositoryMock.create(account);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, client);
        accountService.makeDeposit(accountNumber, 1000);

        Optional<IAccount> accountOptional = accountService.get(accountNumber);
        Assertions.assertThat(accountOptional.get().getBalance()).isEqualTo(2000);
    }

    @Test
    public void anAdvisorShouldBeAbleToMakeADepositOnClientAccount() throws Exception {

        IUser client = new User("client", "password", Role.CLIENT);
        Account account = new Account(client);
        account.setBalance(1000);
        String accountNumber = accountRepositoryMock.create(account);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, advisor);
        accountService.makeDeposit(accountNumber, 1000);

        Optional<IAccount> accountOptional = accountService.get(accountNumber);
        Assertions.assertThat(accountOptional.get().getBalance()).isEqualTo(2000);
    }

    @Test(expected = InsufficientRoleException.class)
    public void aClientShouldNotBeAbleToMakeADepositOnAnotherClientAccount() throws Exception {

        IUser client1 = new User("client1", "password", Role.CLIENT);

        IUser client2 = new User("client2", "password", Role.CLIENT);
        Account account2 = new Account(client2);
        String accountNumber2 = accountRepositoryMock.create(account2);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, client1);
        accountService.makeDeposit(accountNumber2, 1000);

        Assertions.fail("Exception should be raised");
    }
    //endregion

    //region User Story 4 : withdrawal
    @Test
    public void aClientShouldBeAbleToMakeAWithdrawalOnHisAccount() throws Exception {

        IUser client = new User("client", "password", Role.CLIENT);
        Account account = new Account(client);
        account.setBalance(1000);
        String accountNumber = accountRepositoryMock.create(account);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, client);
        accountService.makeWithdrawal(accountNumber, 1000);

        Optional<IAccount> accountOptional = accountService.get(accountNumber);
        Assertions.assertThat(accountOptional.get().getBalance()).isEqualTo(0);
    }

    @Test
    public void aClientShouldBeAbleToMakeAWithdrawalOnClientAccount() throws Exception {

        IUser client = new User("client", "password", Role.CLIENT);
        Account account = new Account(client);
        account.setBalance(1000);
        String accountNumber = accountRepositoryMock.create(account);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, advisor);
        accountService.makeWithdrawal(accountNumber, 1000);

        Optional<IAccount> accountOptional = accountService.get(accountNumber);
        Assertions.assertThat(accountOptional.get().getBalance()).isEqualTo(0);
    }

    @Test(expected = InsufficientRoleException.class)
    public void aClientShouldNotBeAbleToMakeAWithdrawalOnAnotherClientAccount() throws Exception {

        IUser client1 = new User("client1", "password", Role.CLIENT);

        IUser client2 = new User("client2", "password", Role.CLIENT);
        Account account2 = new Account(client2);
        String accountNumber2 = accountRepositoryMock.create(account2);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, client1);
        accountService.makeWithdrawal(accountNumber2, 1000);

        Assertions.fail("Exception should be raised");
    }

    @Test(expected = OverdraftNotAuthorizedException.class)
    public void aClientShouldNotBeAbleToMakeAWithdrawalWhenResultingBalanceIsNegative() throws Exception {

        IUser client = new User("client1", "password", Role.CLIENT);
        Account account = new Account(client);
        String accountNumber = accountRepositoryMock.create(account);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, client);
        accountService.makeWithdrawal(accountNumber, 1000);

        Assertions.fail("Exception should be raised");
    }
    //endregion

    //region User Story 5 : Agreed overdraft
    @Test
    public void anAdvisorShouldBeAbleToSetAgreedOverdraftForAnyClient() throws Exception {

        IUser client = new User("client1", "password", Role.CLIENT);
        Account account = new Account(client);
        String accountNumber = accountRepositoryMock.create(account);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, client);
        accountService.setAgreedOverdraft(accountNumber, -1000);

        Optional<IAccount> accountOptional = accountService.get(accountNumber);
        Assertions.assertThat(accountOptional.get().getAgreedOverdraft()).isEqualTo(-1000);
    }

    @Test
    public void aClientShouldBeAbleToSetAgreedOverdraftForHisOwnAccount() throws Exception {

        IUser client = new User("client1", "password", Role.CLIENT);
        Account account = new Account(client);
        String accountNumber = accountRepositoryMock.create(account);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, client);
        accountService.setAgreedOverdraft(accountNumber, -1000);

        Optional<IAccount> accountOptional = accountService.get(accountNumber);
        Assertions.assertThat(accountOptional.get().getAgreedOverdraft()).isEqualTo(-1000);
    }

    @Test(expected = InsufficientRoleException.class)
    public void aClientShouldNotBeAbleToSetAgreedOverdraftForAnotherClient() throws Exception {

        IUser client1 = new User("client1", "password", Role.CLIENT);

        IUser client2 = new User("client2", "password", Role.CLIENT);
        Account account2 = new Account(client2);
        String accountNumber2 = accountRepositoryMock.create(account2);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, client1);
        accountService.setAgreedOverdraft(accountNumber2, -1000);

        Assertions.fail("Exception should be raised");
    }

    @Test
    public void aClientShouldBeAbleToMakeAWithdrawalWhenResultIsGreaterThanAgreedOverdraft() throws Exception {

        IUser client = new User("client1", "password", Role.CLIENT);
        Account account = new Account(client);
        account.setAgreedOverdraft(-1000);
        String accountNumber = accountRepositoryMock.create(account);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, client);
        accountService.makeWithdrawal(accountNumber, 909);

        Optional<IAccount> accountOptional = accountService.get(accountNumber);
        Assertions.assertThat(accountOptional.get().getBalance()).isEqualTo(-999.9);
    }

    @Test(expected = OverdraftNotAuthorizedException.class)
    public void aClientShouldNotBeAbleToMakeAWithdrawalWhenResultIsLessThanAgreedOverdraft() throws Exception {

        IUser client = new User("client1", "password", Role.CLIENT);
        Account account = new Account(client);
        account.setAgreedOverdraft(-1000);
        String accountNumber = accountRepositoryMock.create(account);

        IAccountService accountService = AccountServiceFactory.getInstance().create(accountRepositoryMock, client);
        accountService.makeWithdrawal(accountNumber, 910); // -1001
    }
    //endregion
}
