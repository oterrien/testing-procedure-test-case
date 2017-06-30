package com.test.userservice.business;

import com.test.User;
import com.test.userservice.api.IUserService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserServiceWithAuthorization implements IUserService {

    private final UserService userService;
    private final User currentUser;

    @Override
    public int create(User user) {
        if (currentUser.getRole() != User.Role.ADMIN) {
            throw new NotAuthorizedException();
        }
        return userService.create(user);
    }

    @Override
    public Optional<User> get(int id) {
        if (currentUser.getRole() != User.Role.ADMIN) {
            throw new NotAuthorizedException();
        }
        return userService.get(id);
    }

    public static class NotAuthorizedException extends RuntimeException {

    }
}
