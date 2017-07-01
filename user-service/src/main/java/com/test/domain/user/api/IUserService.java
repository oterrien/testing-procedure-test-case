package com.test.domain.user.api;

import java.util.Optional;

public interface IUserService {

    Optional<IUser> get(int id);

    int create(IUser user);

    void update(int id, IUser user);

    void delete(int id);

    void resetPassword(int id, String newPassword);
}
