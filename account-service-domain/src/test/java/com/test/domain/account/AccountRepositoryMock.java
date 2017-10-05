package com.test.domain.account;

import com.test.domain.account.api.model.IAccount;
import com.test.domain.account.spi.IAccountRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class AccountRepositoryMock implements IAccountRepository {

    private final List<IAccount> accounts = new ArrayList<>();

    @Override
    public Optional<IAccount> find(String number) {

        return accounts.stream().
                filter(acc -> acc.getNumber().equals(number)).
                map(p -> (Account) p).
                map(Account::clone).
                findAny();
    }

    @Override
    public List<IAccount> findAll() {
        return accounts.stream().
                map(p -> (Account) p).
                map(Account::clone).
                collect(Collectors.toList());
    }

    @Override
    public String create(IAccount account) {

        String number = UUID.randomUUID().toString();
        ((Account) account).setNumber(number);
        accounts.add(account);
        return number;
    }

    @Override
    public void update(String number, IAccount account) {

        delete(number);
        accounts.add(account);
    }

    @Override
    public void delete(String number) {
        accounts.removeIf(acc -> acc.getNumber().equals(number));
    }

    public void deleteAll() {
        accounts.clear();
    }
}
