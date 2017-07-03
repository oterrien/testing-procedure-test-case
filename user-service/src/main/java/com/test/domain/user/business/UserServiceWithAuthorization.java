package com.test.domain.user.business;

import com.test.domain.user.api.IUser;
import com.test.domain.user.api.IUserService;
import com.test.domain.user.api.UserRole;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserServiceWithAuthorization<T extends IUser> implements IUserService<T> {

    private final IUserService<T> userService;
    private final T currentUser;

    @Override
    public Optional<T> get(int id) {

        if (currentUser.getRole() != UserRole.ADMIN && currentUser.getId() != id) {
            throw new NotAuthorizedException("Only admin are able to retrieve user with id " + id);
        }

        return userService.get(id);
    }

    @Override
    public int create(T user) {

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new NotAuthorizedException("Only admin are able to create a new user");
        }

        return userService.create(user);
    }

    @Override
    public void update(int id, T user) {

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new NotAuthorizedException("Only admin are able to update the user with id " + id);
        }

        userService.update(id, user);
    }

    @Override
    public void delete(int id) {

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new NotAuthorizedException("Only admin are able to delete the user with id " + id);
        }

        userService.delete(id);
    }

    @Override
    public void resetPassword(int id, String newPassword) {

        if (currentUser.getId() != id) {
            throw new NotAuthorizedException("Only user with id " + id + " is able to update his password");
        }

        userService.resetPassword(id, newPassword);
    }

    @Override
    public boolean isPasswordCorrect(int id, String password) {
        if (currentUser.getId() != id) {
            throw new NotAuthorizedException("Only user with id " + id + " is able to check his password");
        }

        return userService.isPasswordCorrect(id, password);
    }

    public static class NotAuthorizedException extends RuntimeException {
        public NotAuthorizedException(String message) {
            super(message);
        }
    }
}
