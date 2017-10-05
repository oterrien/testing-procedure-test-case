package com.test.domain.user.spi;

import java.util.List;
import java.util.Optional;

public interface IUserRepository<TU extends IUser> {

    Optional<TU> find(long id);

    List<TU> findAll();

    long create(TU user);

    void update(long id, TU user);

    void delete(long id);
}
