package com.test.domain.user;

import com.test.domain.user.spi.IUserRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class UserRepositoryMock implements IUserRepository<User> {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Optional<User> read(int id) {
        return Optional.ofNullable(clone(users.get(id)));
    }

    @Override
    public int create(User user) {

        int id = users.size() + 1;
        user.setId(id);
        users.put(id, user);
        return id;
    }

    @Override
    public void update(int id, User user) {

        delete(id);
        user.setId(id);
        users.put(id, user);
    }

    @Override
    public void delete(int id) {
        users.remove(id);
    }

    private User clone(User user) {
        User clonedUser = new User();
        clonedUser.setId(user.getId());
        clonedUser.setLogin(user.getLogin());
        clonedUser.setPassword(user.getPassword());
        clonedUser.setRole(user.getRole());
        return clonedUser;
    }
}
