package com.test.domain.user.api;

import com.test.domain.user.spi.IPassword;
import com.test.domain.user.spi.IUser;

import java.util.List;
import java.util.Optional;

public interface IUserService<TU extends IUser> {

    Optional<TU> get(long id);

    List<TU> getAll();

    long create(TU user);

    void update(long id, TU user);

    void delete(long id);

    void resetPassword(long id, IPassword newPassword);

    boolean isPasswordCorrect(long id, IPassword password);

    void addRole(long id, IUser.Role role);

    void removeRole(long id, IUser.Role role);

}
