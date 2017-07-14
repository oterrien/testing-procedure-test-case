package com.test.domain.user.business;

import com.test.domain.user.api.exception.UserActionNotAuthorizedException;
import com.test.domain.user.api.model.IPassword;
import com.test.domain.user.api.model.IUser;
import com.test.domain.user.api.model.Role;
import com.test.domain.user.api.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class UserAuthorizationService<T extends IUser> implements IUserService<T> {

    private final IUserService<T> userService;
    private final T currentUser;

    @Override
    public Optional<T> get(int id) {

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
    public int create(T user) {

        if (!currentUser.hasRole(Role.ADMIN)) {
            throw new UserActionNotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to create a new user");
        }

        return userService.create(user);
    }

    @Override
    public void update(int id, T user) {

        if (!currentUser.hasRole(Role.ADMIN)) {
            throw new UserActionNotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to update the user #" + id);
        }

        userService.update(id, user);
    }

    @Override
    public void delete(int id) {

        if (!currentUser.hasRole(Role.ADMIN)) {
            throw new UserActionNotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to delete the user #" + id);
        }

        userService.delete(id);
    }

    @Override
    public void resetPassword(int id, IPassword newPassword) {

        if (!currentUser.hasRole(Role.ADMIN) && currentUser.getId() != id) {
            throw new UserActionNotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to reset password of user #" + id);
        }

        userService.resetPassword(id, newPassword);
    }

    @Override
    public boolean isPasswordCorrect(int id, IPassword password) {

        if (!currentUser.hasRole(Role.ADMIN) && currentUser.getId() != id) {
            throw new UserActionNotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to check password of user #" + id);
        }

        return userService.isPasswordCorrect(id, password);
    }

    @Override
    public void addRole(int id, Role role) {

        if (!currentUser.hasRole(Role.ADMIN) && currentUser.getId() != id) {
            throw new UserActionNotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to add any role to user #" + id);
        }

        userService.addRole(id, role);
    }

    @Override
    public void removeRole(int id, Role role) {

        if (!currentUser.hasRole(Role.ADMIN) && currentUser.getId() != id) {
            throw new UserActionNotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to remove any role to user #" + id);
        }

        userService.removeRole(id, role);
    }
}
