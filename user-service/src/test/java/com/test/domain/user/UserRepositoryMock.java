package com.test.domain.user;

import com.test.domain.user.spi.IUserRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.test.domain.user.api.IUser.Role;

@RequiredArgsConstructor
public class UserRepositoryMock implements IUserRepository<User> {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Optional<User> find(int id) {
        return Optional.ofNullable(users.get(id)).map(User::clone);
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().map(User::clone).collect(Collectors.toList());
    }

    public int create(String login, String password, Role role) {
        return create(new User(login, new User.Password(password), role));
    }

    @Override
    public int create(User user) {

        int id = users.size() + 1;
        user.setPassword(user.getPassword().encoded());
        user.setId(id);
        users.put(id, user);
        return id;
    }

    @Override
    public void update(int id, User user) {

        delete(id);
        user.setPassword(user.getPassword().encoded());
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
