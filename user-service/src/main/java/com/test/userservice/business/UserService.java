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
    public int create(User user) {
        return userRepository.create(user);
    }

    @Override
    public Optional<User> get(int id) {
        return userRepository.get(id);
    }
}
