package com.test;

import com.test.userservice.api.IUserService;
import com.test.userservice.business.UserService;
import com.test.userservice.business.UserServiceWithAuthorization;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Optional;

import static com.test.User.Role.ADMIN;
import static com.test.User.Role.CLIENT;

public class UserServiceTest {

    private IUserService userService = new UserService(new UserRepositoryMock());

    @Test
    public void a_user_should_be_able_to_be_created() {

        User admin = new User();
        admin.setLogin("anAdmin");
        admin.setPassword("hisPassword");
        admin.setRole(ADMIN);

        int id = userService.create(admin);
        Assertions.assertThat(id).isPositive();

        Optional<User> user1 = userService.get(id);

        Assertions.assertThat(user1).isPresent();
        Assertions.assertThat(user1.orElse(null)).isEqualTo(admin);
    }

    @Test
    public void an_admin_should_be_able_to_create_any_user() {

        User admin = new User();
        admin.setLogin("anAdmin");
        admin.setPassword("hisPassword");
        admin.setRole(ADMIN);

        IUserService userService = new UserServiceWithAuthorization(this.userService, admin);

        User anotherAdmin = new User();
        anotherAdmin.setLogin("anotherAdmin");
        anotherAdmin.setPassword("hisPassword");
        anotherAdmin.setRole(ADMIN);

        int id = userService.create(anotherAdmin);
        Assertions.assertThat(id).isPositive();

        Optional<User> user1 = userService.get(id);

        Assertions.assertThat(user1).isPresent();
    }

    @Test(expected = UserServiceWithAuthorization.NotAuthorizedException.class)
    public void a_non_admin_should_not_be_able_to_create_a_user() {

        User client = new User();
        client.setLogin("aClient");
        client.setPassword("hisPassword");
        client.setRole(CLIENT);

        IUserService userService = new UserServiceWithAuthorization(this.userService, client);

        User user = new User();
        user.setLogin("anAdmin");
        user.setPassword("hisPassword");
        user.setRole(ADMIN);

        userService.create(user);

        Assertions.fail("NotAuthorizedException should be raised");
    }

    @Test
    public void an_admin_should_be_able_to_update_a_user() {

        User admin = new User();
        admin.setLogin("anAdmin");
        admin.setPassword("hisPassword");
        admin.setRole(ADMIN);

        IUserService userService = new UserServiceWithAuthorization(this.userService, admin);

        User user = new User();
        user.setLogin("anotherAdmin");
        user.setPassword("hisPassword");
        user.setRole(ADMIN);

        int id = userService.create(user);

        Optional<User> user1 = userService.get(id);
        user1.ifPresent(u -> u.setPassword("newPassword"));

        userService.update(user.getId(), user1.get());
    }

    @Test(expected = UserServiceWithAuthorization.NotAuthorizedException.class)
    public void a_user_should_not_be_able_to_update_another_user() {

        User admin = new User();
        admin.setLogin("anAdmin");
        admin.setPassword("hisPassword");
        admin.setRole(ADMIN);

        IUserService userService = new UserServiceWithAuthorization(this.userService, admin);

        User client = new User();
        client.setLogin("aClient");
        client.setPassword("hisPassword");
        client.setRole(CLIENT);

        User anotherClient = new User();
        anotherClient.setLogin("anotherClient");
        anotherClient.setPassword("hisPassword");
        anotherClient.setRole(CLIENT);

        userService.create(client);
        int id = userService.create(anotherClient);

        userService = new UserServiceWithAuthorization(this.userService, client);

        anotherClient.setPassword("newPassword");

        userService.update(id, anotherClient);

        Assertions.fail("NotAuthorizedException should be raised");
    }

    @Test
    public void a_user_should_be_able_to_update_its_password() throws Exception{

        User admin = new User();
        admin.setLogin("anAdmin");
        admin.setPassword("hisPassword");
        admin.setRole(ADMIN);

        IUserService userService = new UserServiceWithAuthorization(this.userService, admin);

        User client = new User();
        client.setLogin("aClient");
        client.setPassword("hisPassword");
        client.setRole(CLIENT);

        int id = userService.create(client);

        client = userService.get(id).get();

        userService = new UserServiceWithAuthorization(this.userService, client);

        userService.resetPassword(id, "newPassword");

        userService = new UserServiceWithAuthorization(this.userService, admin);

        client = userService.get(id).get();

        Assertions.assertThat(client.getPassword()).isEqualTo("newPassword");
    }
}
