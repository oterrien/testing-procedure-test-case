package com.test.domain.user.api;

import java.util.Optional;

public interface IUserService<T extends IUser> {

    Optional<T> get(int id);

    int create(T user);

    void update(int id, T user);

    void delete(int id);

    void resetPassword(int id, String newPassword);

    boolean isPasswordCorrect(int id, String password);
}
