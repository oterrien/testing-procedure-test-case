package com.test.domain.user;

import com.test.domain.user.api.IUserService;
import com.test.domain.user.api.UserActionNotAuthorizedException;
import com.test.domain.user.api.UserServiceFactoryProvider;
import com.test.domain.user.business.UserServiceFactory;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static com.test.domain.user.spi.IUser.Role;

public class UserServiceTest {

    private final UserRepositoryMock userRepositoryMock = new UserRepositoryMock();
    private final User admin = new User("anAdmin", new User.Password("hisPassword"), Role.ADMIN);

    @Before
    public void setUp() {
        long id = userRepositoryMock.create(admin);
        admin.setId(id);
    }

    @After
    public void tearDown() {
        userRepositoryMock.deleteAll();
    }

    //US #1
    @Test
    public void aUserShouldBeAbleToBeCreated() {

        IUserService<User> userService = UserServiceFactoryProvider.getInstance().getUserServiceFactory().create(userRepositoryMock, admin);

        long id = userService.create(this.admin);
        Assertions.assertThat(id).isPositive();

        Optional<User> user1 = userService.get(id);

        Assertions.assertThat(user1).isPresent();
        Assertions.assertThat(user1.orElse(null)).isEqualTo(this.admin);
    }

    //US #1
    @Test
    public void anAdminShouldBeAbleToCreateAnyUser() {

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, this.admin);

        User anotherUser = new User("anotherAdmin", new User.Password("hisPassword"), Role.ADMIN);

        long id = userService.create(anotherUser);
        Assertions.assertThat(id).isPositive();

        Optional<User> user1 = userService.get(id);

        Assertions.assertThat(user1).isPresent();
    }

    //US #1
    @Test(expected = UserActionNotAuthorizedException.class)
    public void aNonAdminShouldNotBeAbleToCreateAUser() {

        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);

        User anotherUser = new User("anotherAdmin", new User.Password("hisPassword"), Role.ADMIN);
        userService.create(anotherUser);

        Assertions.fail("NotAuthorizedException should be raised");
    }

    //US #2
    @Test(expected = UserActionNotAuthorizedException.class)
    public void aNonAdminShouldNotBoAbleToReadAnotherUser() {

        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);

        userService.get(this.admin.getId());

        Assertions.fail("NotAuthorizedException should be raised");
    }

    //US #2
    @Test
    public void aUserShouldBeAbleToReadHisOwnInformation() {

        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);

        Optional<User> result = userService.get(client.getId());

        Assertions.assertThat(result).isPresent();
    }

    //US #2
    @Test
    public void aNonAdminShouldNotBeAbleToReadManyUsersExceptedHim() {

        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        User anotherClient = new User("anotherClient", new User.Password("hisPassword"), Role.CLIENT);
        anotherClient.setId(userRepositoryMock.create(anotherClient));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);

        List<User> users = userService.getAll();

        Assertions.assertThat(users.size()).isEqualTo(1);
        Assertions.assertThat(users.get(0)).isEqualTo(client);
    }

    //US #2
    @Test
    public void anAdminShouldBeAbleToReadManyUsers() {

        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        User anotherClient = new User("anotherClient", new User.Password("hisPassword"), Role.CLIENT);
        anotherClient.setId(userRepositoryMock.create(anotherClient));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, this.admin);

        List<User> users = userService.getAll();

        Assertions.assertThat(users).contains(client, anotherClient);
    }

    //US #3
    @Test
    public void anAdminShouldBeAbleToUpdateAnyUser() {

        User anotherUser = new User("anotherAdmin", new User.Password("hisPassword"), Role.ADMIN);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, this.admin);

        anotherUser.setPassword(new User.Password("newPassword", false));
        userService.update(anotherUser.getId(), anotherUser);

        anotherUser = userRepositoryMock.find(anotherUser.getId()).get();
        Assertions.assertThat(anotherUser.getPassword()).isEqualByComparingTo(new User.Password("newPassword", false));
    }

    //US #3
    @Test(expected = UserActionNotAuthorizedException.class)
    public void aNonAdminShouldNotBeAbleToUpdateAnyUser() {

        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        User anotherUser = new User("anotherUser", new User.Password("hisPassword"), Role.ADVISOR);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);

        anotherUser.setPassword(new User.Password("newPassword"));

        userService.update(anotherUser.getId(), anotherUser);

        Assertions.fail("NotAuthorizedException should be raised");
    }

    //US #4
    @Test
    public void aUserShouldBeAbleToUpdateHisOwnPassword() throws Exception {

        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);
        userService.resetPassword(client.getId(), new User.Password("newPassword"));

        client = userRepositoryMock.find(client.getId()).get();

        Assertions.assertThat(client.getPassword()).isEqualByComparingTo(new User.Password("newPassword"));
    }

    //US #4
    @Test(expected = UserActionNotAuthorizedException.class)
    public void aUserShouldNotBeAbleToUpdateAnotherUserPassword() throws Exception {

        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        User anotherUser = new User("anotherUser", new User.Password("hisPassword"), Role.ADVISOR);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);
        userService.resetPassword(anotherUser.getId(), new User.Password("newPassword"));

        Assertions.fail("NotAuthorizedException should be raised");
    }

    //US #5
    @Test
    public void anAdminShouldBeAbleToDeleteAnyUser() throws Exception {

        User anotherUser = new User("anotherAdmin", new User.Password("hisPassword"), Role.ADMIN);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, this.admin);

        userService.delete(anotherUser.getId());
        Assertions.assertThat(userRepositoryMock.find(anotherUser.getId()).orElse(null)).isNull();
    }

    //US #5
    @Test(expected = UserActionNotAuthorizedException.class)
    public void aNonAdminShouldNotBeAbleToDeleteAUser() throws Exception {

        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        User anotherUser = new User("anotherUser", new User.Password("hisPassword"), Role.ADVISOR);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);
        userService.delete(anotherUser.getId());

        Assertions.fail("NotAuthorizedException should be raised");
    }

    //US #6

    @Test
    public void aUserCouldCheckHisPasswordIsCorrect() {

        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);
        boolean isCorrect = userService.isPasswordCorrect(client.getId(), new User.Password("hisPassword"));

        Assertions.assertThat(isCorrect).isTrue();
    }

    @Test
    public void aUserCouldCheckHisPasswordIsNotCorrect() {

        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);
        boolean isCorrect = userService.isPasswordCorrect(client.getId(), new User.Password("bad_password"));

        Assertions.assertThat(isCorrect).isFalse();
    }

    @Test(expected = UserActionNotAuthorizedException.class)
    public void aUserShouldNotBeAbleToCheckPasswordOfAnotherUser() {

        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        User anotherUser = new User("anotherUser", new User.Password("hisPassword"), Role.ADVISOR);
        anotherUser.setId(userRepositoryMock.create(anotherUser));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);
        userService.isPasswordCorrect(anotherUser.getId(), new User.Password("hisPassword"));

        Assertions.fail("NotAuthorizedException should be raised");
    }

    @Ignore
    @Test
    public void anAdminCanAddMoreThanOneRoleToAnotherUser() {

        User client = new User("aClient", new User.Password("histPassword"), Role.CLIENT);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);
        userService.addRole(client.getId(), Role.ADMIN);

        client = userRepositoryMock.find(client.getId()).get();
        Assertions.assertThat(client.getRoles().size()).isEqualTo(2);
        Assertions.assertThat(client.getRoles()).contains(Role.CLIENT, Role.ADMIN);
    }

    @Test
    public void anAdminCanRemoveMoreThanOneRoleToAnotherUser() {

        User client = new User("aClient", new User.Password("histPassword"), Role.CLIENT, Role.ADMIN);
        client.setId(userRepositoryMock.create(client));

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);
        userService.removeRole(client.getId(), Role.ADMIN);

        client = userRepositoryMock.find(client.getId()).get();
        Assertions.assertThat(client.getRoles().size()).isEqualTo(1);
        Assertions.assertThat(client.getRoles()).contains(Role.CLIENT);
    }
}
