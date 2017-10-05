package com.test.domain.user;

import com.test.domain.user.spi.IUserRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class UserRepositoryMock implements IUserRepository<User> {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Optional<User> find(long id) {
        return Optional.ofNullable(users.get(id)).map(User::clone);
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().map(User::clone).collect(Collectors.toList());
    }

    @Override
    public long create(User user) {

        long id = users.size() + 1;
        user.setPassword(user.getPassword().encoded());
        user.setId(id);
        users.put(id, user);
        return id;
    }

    @Override
    public void update(long id, User user) {

        delete(id);
        user.setPassword(user.getPassword().encoded());
        user.setId(id);
        users.put(id, user);
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    public void deleteAll() {
        users.clear();
    }
}
