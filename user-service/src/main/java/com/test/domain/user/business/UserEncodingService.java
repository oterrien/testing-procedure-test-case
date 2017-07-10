package com.test.domain.user.business;

import com.test.domain.user.api.IPassword;
import com.test.domain.user.api.IUser;
import com.test.domain.user.api.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class UserEncodingService<TU extends IUser> implements IUserService<TU> {

    private final IUserService<TU> userService;

    @Override
    public Optional<TU> get(int id) {
        return userService.get(id);
    }


    @Override
    public List<TU> getAll() {
        return userService.getAll();
    }

    @Override
    public int create(TU user) {

        user.setPassword(user.getPassword().encoded());
        return userService.create(user);
    }

    @Override
    public void update(int id, TU user) {

        user.setPassword(user.getPassword().encoded());
        userService.update(id, user);
    }

    @Override
    public void delete(int id) {
        userService.delete(id);
    }

    @Override
    public void resetPassword(int id, IPassword newPassword) {

        userService.resetPassword(id, newPassword.encoded());
    }

    @Override
    public boolean isPasswordCorrect(int id, IPassword password) {

        return userService.isPasswordCorrect(id, password);
    }
}
