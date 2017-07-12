package com.test.domain.user.api.service;

import com.test.domain.user.api.model.IPassword;
import com.test.domain.user.api.model.IUser;
import com.test.domain.user.api.model.Role;

import java.util.List;
import java.util.Optional;

public interface IUserService<TU extends IUser> {

    Optional<TU> get(int id);

    List<TU> getAll();

    int create(TU user);

    void update(int id, TU user);

    void delete(int id);

    void resetPassword(int id, IPassword newPassword);

    boolean isPasswordCorrect(int id, IPassword password);

    void addRole(int id, Role role);

    void removeRole(int id, Role role);

}
