package com.test.infra.user.service;

import com.test.domain.user.api.IUserService;
import com.test.domain.user.api.UserActionNotAuthorizedException;
import com.test.domain.user.business.UserServiceFactory;
import com.test.domain.user.spi.IPassword;
import com.test.infra.user.persistence.UserEntity;
import com.test.infra.user.persistence.UserRepositoryServiceAdapter;
import com.test.infra.user.rest.authentication.UserSessionProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static com.test.domain.user.spi.IUser.Role;

@Service
// This bean is instantiated for each new request
@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class UserServiceAdapter implements IUserService<UserEntity> {

    private IUserService<UserEntity> userService;

    public UserServiceAdapter(@Autowired UserRepositoryServiceAdapter repositoryServiceAdapter,
                              @Autowired UserSessionProviderService<UserEntity> userSessionProviderService) {

        UserEntity currentUser = userSessionProviderService.getUser().
                orElseThrow(() -> new UserActionNotAuthorizedException("No user in session"));

        userService = UserServiceFactory.getInstance().create(repositoryServiceAdapter, currentUser);
    }

    @Override
    public Optional<UserEntity> get(long id) {
        return userService.get(id);
    }

    @Override
    public List<UserEntity> getAll() {
        return userService.getAll();
    }

    @Override
    public long create(UserEntity user) {
        return userService.create(user);
    }

    @Override
    public void update(long id, UserEntity user) {
        userService.update(id, user);
    }

    @Override
    public void delete(long id) {
        userService.delete(id);
    }

    @Override
    public void resetPassword(long id, IPassword newPassword) {
        userService.resetPassword(id, newPassword);
    }

    @Override
    public boolean isPasswordCorrect(long id, IPassword password) {
        return userService.isPasswordCorrect(id, password);
    }

    @Override
    public void addRole(long id, Role role) {
        userService.addRole(id, role);
    }

    @Override
    public void removeRole(long id, Role role) {
        userService.removeRole(id, role);
    }
}

