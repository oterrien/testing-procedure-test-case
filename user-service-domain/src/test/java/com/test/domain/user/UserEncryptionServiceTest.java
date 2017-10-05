package com.test.domain.user;

import com.test.domain.user.api.IUserService;
import com.test.domain.user.business.UserServiceFactory;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static com.test.domain.user.spi.IUser.Role;

public class UserEncryptionServiceTest {

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


    @Test
    public void aPasswordShouldBeEncryptedWhenCreated() throws Exception {

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, admin);

        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userService.create(client));

        Optional<User> client2 = userRepositoryMock.find(client.getId());

        Assertions.assertThat(client2).isPresent();

        Assertions.assertThat(client2.get().getPassword()).isEqualByComparingTo(new User.Password("hisPassword"));

    }

    @Test
    public void aPasswordShouldBeEncryptedWhenUpdated() throws Exception {

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, admin);

        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userService.create(client));

        client = userRepositoryMock.find(client.getId()).get();

        client.setPassword(new User.Password("hisPassword"));
        userService.update(client.getId(), client);

        client = userRepositoryMock.find(client.getId()).get();

        Assertions.assertThat(client.getPassword()).isEqualByComparingTo(new User.Password("hisPassword"));

    }

    @Test
    public void aPasswordShouldBeEncryptedWhenReset() throws Exception {

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, admin);
        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userService.create(client));

        userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);
        userService.resetPassword(client.getId(), new User.Password("newPassword"));

        Optional<User> client2 = userRepositoryMock.find(client.getId());
        Assertions.assertThat(client2).isPresent();

        Assertions.assertThat(client2.get().getPassword()).isEqualByComparingTo(new User.Password("newPassword"));
    }

    @Test
    public void whenAPasswordIsCheckedOnlyEncryptedDataAreChecked() throws Exception {

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, admin);
        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userService.create(client));

        userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);
        boolean isPasswordCorrect = userService.isPasswordCorrect(client.getId(), new User.Password("hisPassword"));
        Assertions.assertThat(true).isEqualTo(isPasswordCorrect);

        isPasswordCorrect = userService.isPasswordCorrect(client.getId(), new User.Password("bad password"));
        Assertions.assertThat(false).isEqualTo(isPasswordCorrect);
    }

    @Test
    public void aPasswordShouldNotBeEncryptedTwice() {

        IUserService<User> userService = UserServiceFactory.getInstance().create(userRepositoryMock, admin);
        User client = new User("aClient", new User.Password("hisPassword"), Role.CLIENT);
        client.setId(userService.create(client));

        userService = UserServiceFactory.getInstance().create(userRepositoryMock, client);
        userService.resetPassword(client.getId(), new User.Password("newPassword"));

        client = userRepositoryMock.find(client.getId()).get();

        userService.resetPassword(client.getId(), new User.Password("newPassword"));

        client = userRepositoryMock.find(client.getId()).get();

        boolean isPasswordCorrect = userService.isPasswordCorrect(client.getId(), new User.Password("newPassword"));
        Assertions.assertThat(isPasswordCorrect).isEqualTo(true);
    }
}
