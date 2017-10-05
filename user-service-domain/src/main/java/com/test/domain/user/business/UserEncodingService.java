package com.test.domain.user.business;

import com.test.domain.user.api.IUserService;
import com.test.domain.user.spi.IPassword;
import com.test.domain.user.spi.IUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import static com.test.domain.user.spi.IUser.Role;

@RequiredArgsConstructor
@Slf4j
class UserEncodingService<TU extends IUser> implements IUserService<TU> {

    private final IUserService<TU> userService;

    @Override
    public Optional<TU> get(long id) {
        return userService.get(id);
    }


    @Override
    public List<TU> getAll() {
        return userService.getAll();
    }

    @Override
    public long create(TU user) {

        user.setPassword(user.getPassword().encoded());
        return userService.create(user);
    }

    @Override
    public void update(long id, TU user) {

        user.setPassword(user.getPassword().encoded());
        userService.update(id, user);
    }

    @Override
    public void delete(long id) {
        userService.delete(id);
    }

    @Override
    public void resetPassword(long id, IPassword newPassword) {

        userService.resetPassword(id, newPassword.encoded());
    }

    @Override
    public boolean isPasswordCorrect(long id, IPassword password) {

        return userService.isPasswordCorrect(id, password);
    }

    @Override
    public void addRole(long id, Role role) {
        userService.addRole(id, role);
    }

    @Override
    public void removeRole(long id, Role role) {
        userService.removeRole(id, role);
    }
}
