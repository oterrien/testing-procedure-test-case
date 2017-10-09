package com.test.domain.user.business;

import com.test.domain.user.api.IUserService;
import com.test.domain.user.api.UserActionNotAuthorizedException;
import com.test.domain.user.spi.IPassword;
import com.test.domain.user.spi.IUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.test.domain.user.spi.IUser.Role;

@RequiredArgsConstructor
@Slf4j
class UserAuthorizationService<T extends IUser> implements IUserService<T> {

    private final IUserService<T> userService;
    private final T currentUser;

    @Override
    public Optional<T> get(long id) {

        if (!currentUser.hasRole(Role.ADMIN) && currentUser.getId() != id) {
            throw new UserActionNotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to retrieve user #" + id);
        }

        return userService.get(id);
    }


    @Override
    public List<T> getAll() {

        return !currentUser.hasRole(Role.ADMIN) ?
                Collections.singletonList(currentUser) :
                userService.getAll();
    }

    @Override
    public long create(T user) {

        if (!currentUser.hasRole(Role.ADMIN)) {
            throw new UserActionNotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to create a new user");
        }

        return userService.create(user);
    }

    @Override
    public void update(long id, T user) {

        if (!currentUser.hasRole(Role.ADMIN)) {
            throw new UserActionNotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to update the user #" + id);
        }

        userService.update(id, user);
    }

    @Override
    public void delete(long id) {

        if (!currentUser.hasRole(Role.ADMIN)) {
            throw new UserActionNotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to delete the user #" + id);
        }

        userService.delete(id);
    }

    @Override
    public void resetPassword(long id, IPassword newPassword) {

        if (!currentUser.hasRole(Role.ADMIN) && currentUser.getId() != id) {
            throw new UserActionNotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to reset password of user #" + id);
        }

        userService.resetPassword(id, newPassword);
    }

    @Override
    public boolean isPasswordCorrect(long id, IPassword password) {

        if (!currentUser.hasRole(Role.ADMIN) && currentUser.getId() != id) {
            throw new UserActionNotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to check password of user #" + id);
        }

        return userService.isPasswordCorrect(id, password);
    }

    @Override
    public void addRole(long id, Role role) {

        if (!currentUser.hasRole(Role.ADMIN)) {
            throw new UserActionNotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to add any role to user #" + id);
        }

        userService.addRole(id, role);
    }

    @Override
    public void removeRole(long id, Role role) {

        if (!currentUser.hasRole(Role.ADMIN)) {
            throw new UserActionNotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to remove any role to user #" + id);
        }

        userService.removeRole(id, role);
    }
}
