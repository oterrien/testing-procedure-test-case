package com.test.domain.account.spi;

import com.test.domain.account.api.model.IAccount;

import java.util.List;
import java.util.Optional;

public interface IAccountRepository {

    Optional<IAccount> find(String accountNumber);

    List<IAccount> findAll();

    String create(IAccount user);

    void update(String accountNumber, IAccount account);

    void delete(String accountNumber);
}
