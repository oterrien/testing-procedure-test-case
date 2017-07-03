package com.test.domain.user;

import com.test.domain.user.api.UserRole;
import com.test.domain.user.spi.IUserRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class UserRepositoryMock implements IUserRepository<User> {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Optional<User> find(int id) {
        return Optional.ofNullable(users.get(id)).map(User::clone);
    }

    public int create(String login, String password, UserRole role) {
        return create(new User(login, password, role));
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

    public void deleteAll() {
        users.clear();
    }
}
