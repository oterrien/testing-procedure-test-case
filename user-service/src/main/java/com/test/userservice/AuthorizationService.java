package com.test.userservice;

import com.test.User;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class AuthorizationService implements IUserService {

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
