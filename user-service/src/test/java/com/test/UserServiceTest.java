package com.test;

import com.test.userservice.*;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Optional;

import static com.test.User.Role.ADMIN;
import static com.test.User.Role.CLIENT;

public class UserServiceTest {

    @Test
    public void a_user_should_be_able_to_be_created() {

        User user = new User("newUser");
        user.setPassword("aPassword");
        user.setRole(ADMIN);

        IUserService userService = new UserService(new UserRepositoryMock());
        int id = userService.create(user);

        Optional<User> user1 = userService.get(id);

        Assertions.assertThat(user1).isPresent();
        Assertions.assertThat(user1.orElse(null)).isEqualTo(user);
    }

    @Test
    public void an_administrator_should_be_able_to_create_any_user() {

        User admin = new User("newUser");
        admin.setPassword("aPassword");
        admin.setRole(ADMIN);

        IUserService userService = new AuthorizationService(new UserService(new UserRepositoryMock()), admin);

        User user = new User("newUser");
        user.setPassword("aPassword");
        user.setRole(ADMIN);

        int id = userService.create(user);

        Optional<User> user1 = userService.get(id);

        Assertions.assertThat(user1).isPresent();
    }

    @Test(expected = AuthorizationService.NotAuthorizedException.class)
    public void an_non_administrator_should_not_be_able_to_create_a_user() {

        User client = new User("newUser");
        client.setPassword("aPassword");
        client.setRole(CLIENT);

        IUserService userService = new AuthorizationService(new UserService(new UserRepositoryMock()), client);

        User user = new User("newUser");
        user.setPassword("aPassword");
        user.setRole(ADMIN);

        int id = userService.create(user);

        Assertions.fail("NotAuthorizedException should be raised");
    }
}
