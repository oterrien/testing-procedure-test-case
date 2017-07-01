package com.test.domain.user.business;

import com.test.domain.user.api.IUser;
import com.test.domain.user.api.IUserService;
import com.test.domain.user.spi.IUserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    @Override
    public Optional<IUser> get(int id) {
        return userRepository.get(id);
    }

    @Override
    public int create(IUser user) {
        return userRepository.create(user);
    }

    @Override
    public void update(int id, IUser user) {
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
