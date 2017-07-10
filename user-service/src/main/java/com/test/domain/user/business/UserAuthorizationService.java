package com.test.domain.user.business;

import com.test.domain.user.api.IPassword;
import com.test.domain.user.api.IUser;
import com.test.domain.user.api.IUserService;
import com.test.domain.user.api.NotAuthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.test.domain.user.api.IUser.Role;

@RequiredArgsConstructor
@Slf4j
public class UserAuthorizationService<T extends IUser> implements IUserService<T> {

    private final IUserService<T> userService;
    private final T currentUser;

    @Override
    public Optional<T> get(int id) {

        if (currentUser.getRole() != Role.ADMIN && currentUser.getId() != id) {
            throw new NotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to retrieve user #" + id);
        }

        return userService.get(id);
    }


    @Override
    public List<T> getAll() {

        List<T> allUsers = userService.getAll();

        if (currentUser.getRole() != Role.ADMIN) {
            return allUsers.stream().filter(p -> p.getId() == currentUser.getId()).collect(Collectors.toList());
        } else {
            return userService.getAll();
        }
    }

    @Override
    public int create(T user) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new NotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to create a new user");
        }

        return userService.create(user);
    }

    @Override
    public void update(int id, T user) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new NotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to update the user #" + id);
        }

        userService.update(id, user);
    }

    @Override
    public void delete(int id) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new NotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to delete the user #" + id);
        }

        userService.delete(id);
    }

    @Override
    public void resetPassword(int id, IPassword newPassword) {

        if (currentUser.getRole() != Role.ADMIN && currentUser.getId() != id) {
            throw new NotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to reset password of user #" + id);
        }

        userService.resetPassword(id, newPassword);
    }

    @Override
    public boolean isPasswordCorrect(int id, IPassword password) {

        if (currentUser.getRole() != Role.ADMIN && currentUser.getId() != id) {
            throw new NotAuthorizedException("User " + currentUser.getLogin() + " is not authorized to check password of user #" + id);
        }

        return userService.isPasswordCorrect(id, password);
    }
}
