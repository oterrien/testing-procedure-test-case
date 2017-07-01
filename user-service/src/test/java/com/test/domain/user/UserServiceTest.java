package com.test.domain.user;

import com.test.domain.user.api.IUser;
import com.test.domain.user.api.IUserService;
import com.test.domain.user.business.UserService;
import com.test.domain.user.business.UserServiceWithAuthorization;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static com.test.domain.user.api.IUser.Role.ADMIN;
import static com.test.domain.user.api.IUser.Role.CLIENT;

public class UserServiceTest {

    private IUserService<User> userService;
    private User admin;

    @Before
    public void setUp() {
        this.userService = new UserService<>(new UserRepositoryMock());
        this.admin = new User();
        this.admin.setLogin("anAdmin");
        this.admin.setPassword("hisPassword");
        this.admin.setRole(ADMIN);
    }

    @Test
    public void a_user_should_be_able_to_be_created() {

        int id = this.userService.create(this.admin);
        Assertions.assertThat(id).isPositive();

        Optional<User> user1 = this.userService.get(id);

        Assertions.assertThat(user1).isPresent();
        Assertions.assertThat(user1.orElse(null)).isEqualTo(this.admin);
    }

    @Test
    public void an_admin_should_be_able_to_create_any_user() {

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, this.admin);

        User anotherAdmin = new User();
        anotherAdmin.setLogin("anotherAdmin");
        anotherAdmin.setPassword("hisPassword");
        anotherAdmin.setRole(ADMIN);

        int id = userService.create(anotherAdmin);
        Assertions.assertThat(id).isPositive();

        Optional<User> user1 = this.userService.get(id);

        Assertions.assertThat(user1).isPresent();
    }

    @Test(expected = UserServiceWithAuthorization.NotAuthorizedException.class)
    public void a_non_admin_should_not_be_able_to_create_a_user() {

        User client = new User();
        client.setLogin("aClient");
        client.setPassword("hisPassword");
        client.setRole(CLIENT);

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);

        userService.create(this.admin);

        Assertions.fail("NotAuthorizedException should be raised");
    }

    @Test
    public void an_admin_should_be_able_to_update_a_user() {

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, this.admin);

        User anotherAdmin = new User();
        anotherAdmin.setLogin("anotherAdmin");
        anotherAdmin.setPassword("hisPassword");
        anotherAdmin.setRole(ADMIN);

        int id = userService.create(anotherAdmin);

        Optional<User> anotherAdmin1 = this.userService.get(id);
        anotherAdmin1.ifPresent(u -> u.setPassword("newPassword"));
        userService.update(id, anotherAdmin1.get());

        anotherAdmin1 = userService.get(id);
        Assertions.assertThat(anotherAdmin1.get().getPassword()).isEqualTo("newPassword");

    }

    @Test(expected = UserServiceWithAuthorization.NotAuthorizedException.class)
    public void a_user_should_not_be_able_to_update_another_user() {

        User admin = new User();
        admin.setLogin("anAdmin");
        admin.setPassword("hisPassword");
        admin.setRole(ADMIN);

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, admin);

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

        userService = new UserServiceWithAuthorization<>(this.userService, client);

        anotherClient.setPassword("newPassword");

        userService.update(id, anotherClient);

        Assertions.fail("NotAuthorizedException should be raised");
    }

    @Test
    public void a_user_should_be_able_to_update_its_password() throws Exception {

        User admin = new User();
        admin.setLogin("anAdmin");
        admin.setPassword("hisPassword");
        admin.setRole(ADMIN);

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, admin);

        User client = new User();
        client.setLogin("aClient");
        client.setPassword("hisPassword");
        client.setRole(CLIENT);

        int id = userService.create(client);

        client = userService.get(id).get();

        userService = new UserServiceWithAuthorization<>(this.userService, client);

        userService.resetPassword(id, "newPassword");

        userService = new UserServiceWithAuthorization<>(this.userService, admin);

        client = userService.get(id).get();

        Assertions.assertThat(client.getPassword()).isEqualTo("newPassword");
    }
}
