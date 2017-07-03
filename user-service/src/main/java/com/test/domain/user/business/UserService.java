package com.test.domain.user.business;

import com.test.domain.user.api.IUser;
import com.test.domain.user.api.IUserService;
import com.test.domain.user.spi.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.h2.util.StringUtils;

import java.util.Optional;

@RequiredArgsConstructor
public class UserService<T extends IUser> implements IUserService<T> {

    private final IUserRepository<T> userRepository;

    @Override
    public Optional<T> get(int id) {
        return userRepository.find(id);
    }

    @Override
    public int create(T user) {
        return userRepository.create(user);
    }

    @Override
    public void update(int id, T user) {
        user.setId(id);
        userRepository.update(id, user);
    }

    @Override
    public void delete(int id) {
        userRepository.delete(id);
    }

    @Override
    public void resetPassword(int id, String newPassword) {

        get(id).ifPresent(u -> {
            u.setPassword(newPassword);
            update(id, u);
        });
    }

    @Override
    public boolean isPasswordCorrect(int id, String password) {

        return get(id).
                map(u -> StringUtils.equals(u.getPassword(), password)).
                orElse(false);
    }
}
