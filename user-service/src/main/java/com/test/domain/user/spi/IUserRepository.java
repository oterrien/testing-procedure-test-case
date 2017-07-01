package com.test.domain.user.spi;

import com.test.domain.user.api.IUser;

import java.util.Optional;

public interface IUserRepository {

    Optional<IUser> read(int id);

    int create(IUser user);

    void update(int id, IUser user);

    void delete(int id);
}
