package com.test.domain.user.spi;

import com.test.domain.user.api.IUser;

import java.util.Optional;

public interface IUserRepository<T extends IUser> {

    Optional<T> read(int id);

    int create(T user);

    void update(int id, T user);

    void delete(int id);
}
