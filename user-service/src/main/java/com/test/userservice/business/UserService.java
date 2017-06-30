package com.test.userservice.business;

import com.test.User;
import com.test.userservice.api.IUserService;
import com.test.userservice.spi.IUserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    @Override
    public Optional<User> get(int id) {
        return userRepository.get(id);
    }

    @Override
    public int create(User user) {
        return userRepository.create(user);
    }

    @Override
    public void update(int id, User user) {
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
}
