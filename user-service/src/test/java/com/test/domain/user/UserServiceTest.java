package com.test.domain.user;

import com.test.domain.user.api.IUserService;
import com.test.domain.user.business.UserService;
import com.test.domain.user.business.UserServiceWithAuthorization;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static com.test.domain.user.api.UserRole.*;

public class UserServiceTest {

    private final UserRepositoryMock userRepositoryMock = new UserRepositoryMock();
    private final IUserService<User> userService = new UserService<>(userRepositoryMock);
    private final User admin = new User("anAdmin", "hisPassword", ADMIN);

    @Before
    public void setUp() {
        int id = userRepositoryMock.create(admin);
        admin.setId(id);
    }

    @After
    public void tearDown() {
        userRepositoryMock.delete();
    }

    @Test
    @Ignore("Deprecated")
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

        User anotherUser = new User("anotherAdmin", "hisPassword", ADMIN);

        int id = userService.create(anotherUser);
        Assertions.assertThat(id).isPositive();

        Optional<User> user1 = this.userService.get(id);

        Assertions.assertThat(user1).isPresent();
    }

    @Test(expected = UserServiceWithAuthorization.NotAuthorizedException.class)
    public void a_non_admin_should_not_be_able_to_create_a_user() {

        User client = new User("aClient", "hisPassword", CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);

        User anotherUser = new User("anotherAdmin", "hisPassword", ADMIN);
        userService.create(anotherUser);

        Assertions.fail("NotAuthorizedException should be raised");
    }

    @Test(expected = UserServiceWithAuthorization.NotAuthorizedException.class)
    public void a_non_admin_should_not_be_able_to_read_a_user() {

        User client = new User("aClient", "hisPassword", CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);

        userService.get(this.admin.getId());

        Assertions.fail("NotAuthorizedException should be raised");
    }

    @Test
    public void an_admin_should_be_able_to_update_any_user() {

        User anotherUser = new User("anotherAdmin", "hisPassword", ADMIN);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, this.admin);

        anotherUser.setRole(ADVISOR);
        userService.update(anotherUser.getId(), anotherUser);

        anotherUser = userRepositoryMock.find(anotherUser.getId()).get();
        Assertions.assertThat(anotherUser.getRole()).isEqualTo(ADVISOR);
    }

    @Test(expected = UserServiceWithAuthorization.NotAuthorizedException.class)
    public void a_non_admin_should_not_be_able_to_update_any_user() {

        User client = new User("aClient", "hisPassword", CLIENT);
        client.setId(userRepositoryMock.create(client));

        User anotherUser = new User("anotherUser", "hisPassword", ADVISOR);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);

        anotherUser.setPassword("newPassword");

        userService.update(anotherUser.getId(), anotherUser);

        Assertions.fail("NotAuthorizedException should be raised");
    }

    @Test
    public void a_user_should_be_able_to_update_his_own_password() throws Exception {

        User client = new User("aClient", "hisPassword", CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);
        userService.resetPassword(client.getId(), "newPassword");

        client = userRepositoryMock.find(client.getId()).get();

        Assertions.assertThat(client.getPassword()).isEqualTo("newPassword");
    }


    @Test(expected = UserServiceWithAuthorization.NotAuthorizedException.class)
    public void a_user_should_not_be_able_to_update_another_user_password() throws Exception {

        User client = new User("aClient", "hisPassword", CLIENT);
        client.setId(userRepositoryMock.create(client));

        User anotherUser = new User("anotherUser", "hisPassword", ADVISOR);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);
        userService.resetPassword(anotherUser.getId(), "newPassword");

        Assertions.fail("NotAuthorizedException should be raised");
    }


    @Test
    public void an_admin_should_be_able_to_delete_any_user() throws Exception {

        User anotherUser = new User("anotherAdmin", "hisPassword", ADMIN);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, this.admin);

        userService.delete(anotherUser.getId());
        Assertions.assertThat(userRepositoryMock.find(anotherUser.getId()).orElse(null)).isNull();
    }


    @Test(expected = UserServiceWithAuthorization.NotAuthorizedException.class)
    public void a_non_admin_should_not_be_able_to_delete_a_user() throws Exception {

        User client = new User("aClient", "hisPassword", CLIENT);
        client.setId(userRepositoryMock.create(client));

        User anotherUser = new User("anotherUser", "hisPassword", ADVISOR);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);
        userService.delete(anotherUser.getId());


        Assertions.fail("NotAuthorizedException should be raised");
    }

}
