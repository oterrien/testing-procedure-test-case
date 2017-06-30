package com.test.userservice;

import com.test.User;

import java.util.Optional;

public interface IUserService {

    int create(User user);

    Optional<User> get(int id);
}
