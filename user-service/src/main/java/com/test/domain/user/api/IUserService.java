package com.test.domain.user.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
