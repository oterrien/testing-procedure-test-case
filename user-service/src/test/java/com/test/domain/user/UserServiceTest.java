package com.test.domain.user;

import com.test.domain.user.api.IUserService;
import com.test.domain.user.business.UserService;
import com.test.domain.user.business.UserServiceWithAuthorization;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static com.test.domain.user.api.IUser.Role;


public class UserServiceTest {

    private final UserRepositoryMock userRepositoryMock = new UserRepositoryMock();
    private final IUserService<User> userService = new UserService<>(userRepositoryMock);
    private final User admin = new User("anAdmin", "hisPassword", Role.ADMIN);

    @Before
    public void setUp() {
        int id = userRepositoryMock.create(admin);
        admin.setId(id);
    }

    @After
    public void tearDown() {
        userRepositoryMock.deleteAll();
    }

    //US #1
    @Test
    public void aUserShouldBeAbleToBeCreated() {

        int id = this.userService.create(this.admin);
        Assertions.assertThat(id).isPositive();

        Optional<User> user1 = this.userService.get(id);

        Assertions.assertThat(user1).isPresent();
        Assertions.assertThat(user1.orElse(null)).isEqualTo(this.admin);
    }

    //US #1
    @Test
    public void anAdminShouldBeAbleToCreateAnyUser() {

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, this.admin);

        User anotherUser = new User("anotherAdmin", "hisPassword", Role.ADMIN);

        int id = userService.create(anotherUser);
        Assertions.assertThat(id).isPositive();

        Optional<User> user1 = this.userService.get(id);

        Assertions.assertThat(user1).isPresent();
    }

    //US #1
    @Test(expected = UserServiceWithAuthorization.NotAuthorizedException.class)
    public void aNonAdminShouldNotBeAbleToCreateAUser() {

        User client = new User("aClient", "hisPassword", Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);

        User anotherUser = new User("anotherAdmin", "hisPassword", Role.ADMIN);
        userService.create(anotherUser);

        Assertions.fail("NotAuthorizedException should be raised");
    }

    //US #2
    @Test(expected = UserServiceWithAuthorization.NotAuthorizedException.class)
    public void aNonAdminShouldNotBoAbleToReadAnotherUser() {

        User client = new User("aClient", "hisPassword", Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);

        userService.get(this.admin.getId());

        Assertions.fail("NotAuthorizedException should be raised");
    }

    //US #2
    @Test
    public void aUserShouldBeAbleToReadHisOwnInformation() {

        User client = new User("aClient", "hisPassword", Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);

        Optional<User> result = userService.get(client.getId());

        Assertions.assertThat(result).isPresent();
    }

    //US #2
    @Test
    public void aNonAdminShouldNotBeAbleToReadManyUsersExceptedHim() {

        User client = new User("aClient", "hisPassword", Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        User anotherClient = new User("anotherClient", "hisPassword", Role.CLIENT);
        anotherClient.setId(userRepositoryMock.create(anotherClient));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);

        List<User> users = userService.getAll();

        Assertions.assertThat(users.size()).isEqualTo(1);
        Assertions.assertThat(users.get(0)).isEqualTo(client);
    }

    //US #2
    @Test
    public void anAdminShouldBeAbleToReadManyUsers() {

        User client = new User("aClient", "hisPassword", Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        User anotherClient = new User("anotherClient", "hisPassword", Role.CLIENT);
        anotherClient.setId(userRepositoryMock.create(anotherClient));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, this.admin);

        List<User> users = userService.getAll();

        Assertions.assertThat(users).contains(client, anotherClient);
    }

    //US #3
    @Test
    public void anAdminShouldBeAbleToUpdateAnyUser() {

        User anotherUser = new User("anotherAdmin", "hisPassword", Role.ADMIN);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, this.admin);

        anotherUser.setRole(Role.ADVISOR);
        userService.update(anotherUser.getId(), anotherUser);

        anotherUser = userRepositoryMock.find(anotherUser.getId()).get();
        Assertions.assertThat(anotherUser.getRole()).isEqualTo(Role.ADVISOR);
    }

    //US #3
    @Test(expected = UserServiceWithAuthorization.NotAuthorizedException.class)
    public void aNonAdminShouldNotBeAbleToUpdateAnyUser() {

        User client = new User("aClient", "hisPassword", Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        User anotherUser = new User("anotherUser", "hisPassword", Role.ADVISOR);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);

        anotherUser.setPassword("newPassword");

        userService.update(anotherUser.getId(), anotherUser);

        Assertions.fail("NotAuthorizedException should be raised");
    }

    //US #4
    @Test
    public void aUserShouldBeAbleToUpdateHisOwnPassword() throws Exception {

        User client = new User("aClient", "hisPassword", Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);
        userService.resetPassword(client.getId(), "newPassword");

        client = userRepositoryMock.find(client.getId()).get();

        Assertions.assertThat(client.getPassword()).isEqualTo("newPassword");
    }

    //US #4
    @Test(expected = UserServiceWithAuthorization.NotAuthorizedException.class)
    public void aUserShouldNotBeAbleToUpdateAnotherUserPassword() throws Exception {

        User client = new User("aClient", "hisPassword", Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        User anotherUser = new User("anotherUser", "hisPassword", Role.ADVISOR);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);
        userService.resetPassword(anotherUser.getId(), "newPassword");

        Assertions.fail("NotAuthorizedException should be raised");
    }

    //US #5
    @Test
    public void anAdminShouldBeAbleToDeleteAnyUser() throws Exception {

        User anotherUser = new User("anotherAdmin", "hisPassword", Role.ADMIN);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, this.admin);

        userService.delete(anotherUser.getId());
        Assertions.assertThat(userRepositoryMock.find(anotherUser.getId()).orElse(null)).isNull();
    }

    //US #5
    @Test(expected = UserServiceWithAuthorization.NotAuthorizedException.class)
    public void aNonAdminShouldNotBeAbleToDeleteAUser() throws Exception {

        User client = new User("aClient", "hisPassword", Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        User anotherUser = new User("anotherUser", "hisPassword", Role.ADVISOR);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);
        userService.delete(anotherUser.getId());

        Assertions.fail("NotAuthorizedException should be raised");
    }

    //US #6

    @Test
    public void aUserCouldCheckHisPasswordIsCorrect() {

        User client = new User("aClient", "hisPassword", Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);
        boolean isCorrect = userService.isPasswordCorrect(client.getId(), "hisPassword");

        Assertions.assertThat(isCorrect).isTrue();
    }

    @Test
    public void aUserCouldCheckHisPasswordIsNotCorrect() {

        User client = new User("aClient", "hisPassword", Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);
        boolean isCorrect = userService.isPasswordCorrect(client.getId(), "bad_password");

        Assertions.assertThat(isCorrect).isFalse();
    }

    @Test(expected = UserServiceWithAuthorization.NotAuthorizedException.class)
    public void aUserShouldNotBeAbleToCheckPasswordOfAnotherUser() {

        User client = new User("aClient", "hisPassword", Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        User anotherUser = new User("anotherUser", "hisPassword", Role.ADVISOR);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = new UserServiceWithAuthorization<>(this.userService, client);
        userService.isPasswordCorrect(anotherUser.getId(), "hisPassword");

        Assertions.fail("NotAuthorizedException should be raised");
    }

}
