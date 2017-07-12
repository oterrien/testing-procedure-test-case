package com.test.domain.user.spi;

import com.test.domain.user.api.model.IUser;

import java.util.List;
import java.util.Optional;

public interface IUserRepository<TU extends IUser> {

    Optional<TU> find(int id);

    List<TU> findAll();

    int create(TU user);

    void update(int id, TU user);

    void delete(int id);
}
