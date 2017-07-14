package com.test.domain.user.business;

import com.test.domain.user.api.model.IPassword;
import com.test.domain.user.api.model.IUser;
import com.test.domain.user.api.model.Role;
import com.test.domain.user.api.service.IUserService;
import com.test.domain.user.spi.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class UserService<TU extends IUser> implements IUserService<TU> {

    private final IUserRepository<TU> userRepository;

    @Override
    public Optional<TU> get(int id) {
        if (log.isTraceEnabled()) {
            log.trace("Finding the user {}", id);
        }

        return userRepository.find(id);
    }

    @Override
    public List<TU> getAll() {
        if (log.isTraceEnabled()) {
            log.trace("Finding all users");
        }

        return userRepository.findAll();
    }

    @Override
    public int create(TU user) {
        if (log.isTraceEnabled()) {
            log.trace("Creating a new user");
        }

        return Optional.ofNullable(user).
                map(u -> userRepository.create(user)).
                orElse(0);
    }

    @Override
    public void update(int id, TU user) {
        if (log.isTraceEnabled()) {
            log.trace("Updating the user {}", id);
        }

        Optional.ofNullable(user).
                ifPresent(u -> {
                    user.setId(id);
                    userRepository.update(id, user);
                });
    }

    @Override
    public void delete(int id) {
        if (log.isTraceEnabled()) {
            log.trace("Deleting the user {}", id);
        }
        userRepository.delete(id);
    }

    @Override
    public void resetPassword(int id, IPassword newPassword) {
        if (log.isTraceEnabled()) {
            log.trace("Resetting password of user {}", id);
        }

        get(id).ifPresent(u -> {
            u.setPassword(newPassword);
            update(id, u);
        });
    }

    @Override
    public boolean isPasswordCorrect(int id, IPassword password) {
        if (log.isTraceEnabled()) {
            log.trace("Checking password of user {}", id);
        }

        return get(id).
                map(u -> u.isSamePassword(password)).
                orElse(false);
    }

    @Override
    public void addRole(int id, Role role) {
        if (log.isTraceEnabled()) {
            log.trace("Adding role to user {}", id);
        }

        get(id).filter(u -> !u.hasRole(role)).
                ifPresent(u -> {
                    u.addRole(role);
                    update(id, u);

                });
    }

    @Override
    public void removeRole(int id, Role role) {
        if (log.isTraceEnabled()) {
            log.trace("Removing role from user {}", id);
        }

        get(id).filter(u -> u.hasRole(role)).
                ifPresent(u -> {
                    u.removeRole(role);
                    update(id, u);
                });
    }
}
