package com.test.userservice.spi;

import com.test.User;

import java.util.Optional;

public interface IUserRepository {

    Optional<User> get(int id);

    int create(User user);

    void update(int id, User user);

    void delete(int id);
}
