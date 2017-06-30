package com.test.userservice.api;

import com.test.User;

import java.util.Optional;

public interface IUserService {

    Optional<User> get(int id);

    int create(User user);

    void update(int id, User user);

    void delete(int id);

    void resetPassword(int id, String newPassword);
}
