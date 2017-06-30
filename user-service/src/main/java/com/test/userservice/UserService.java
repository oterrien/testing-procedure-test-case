package com.test.userservice;

import com.test.User;
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
