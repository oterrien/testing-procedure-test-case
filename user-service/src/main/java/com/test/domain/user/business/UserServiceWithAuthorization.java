package com.test.domain.user.business;

import com.test.domain.user.api.IUser;
import com.test.domain.user.api.IUserService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserServiceWithAuthorization implements IUserService {

    private final IUserService userService;
    private final IUser currentUser;

    @Override
    public Optional<IUser> get(int id) {

        if (currentUser.getRole() != IUser.Role.ADMIN) {
            throw new NotAuthorizedException("Only admin are able to retrieve user with id " + id);
        }

        return userService.get(id);
    }

    @Override
    public int create(IUser user) {

        if (currentUser.getRole() != IUser.Role.ADMIN) {
            throw new NotAuthorizedException("Only admin are able to create a new user");
        }

        return userService.create(user);
    }

    @Override
    public void update(int id, IUser user) {
        if (currentUser.getRole() != IUser.Role.ADMIN) {
            throw new NotAuthorizedException("Only admin are able to update the user with id " + id);
        }

        userService.update(id, user);
    }

    @Override
    public void delete(int id) {

        if (currentUser.getRole() != IUser.Role.ADMIN) {
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

    public static class NotAuthorizedException extends RuntimeException {
        public NotAuthorizedException(String message) {
            super(message);
        }
    }
}
