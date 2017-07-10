package com.test.infra.user.service;

import com.test.domain.user.api.IPassword;
import com.test.domain.user.api.IUserService;
import com.test.domain.user.api.NotAuthorizedException;
import com.test.domain.user.api.UserServiceFactory;
import com.test.domain.user.business.UserAuthorizationService;
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

@Service
@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
// This bean is instantiated for each new request
@Slf4j
public class UserServiceAdapter implements IUserService<UserEntity> {

    private IUserService<UserEntity> userService;

    public UserServiceAdapter(@Autowired UserRepositoryServiceAdapter repositoryServiceAdapter,
                              @Autowired UserSessionProviderService<UserEntity> userSessionProviderService) {

        UserEntity currentUser = userSessionProviderService.getUser().
                orElseThrow(() -> new NotAuthorizedException("No user in session"));

        userService = UserServiceFactory.getInstance().create(repositoryServiceAdapter, currentUser);
    }

    @Override
    public Optional<UserEntity> get(int id) {
        return userService.get(id);
    }

    @Override
    public List<UserEntity> getAll() {
        return userService.getAll();
    }

    @Override
    public int create(UserEntity user) {
        return userService.create(user);
    }

    @Override
    public void update(int id, UserEntity user) {
        userService.update(id, user);
    }

    @Override
    public void delete(int id) {
        userService.delete(id);
    }

    @Override
    public void resetPassword(int id, IPassword newPassword) {
        userService.resetPassword(id, newPassword);
    }

    @Override
    public boolean isPasswordCorrect(int id, IPassword password) {
        return userService.isPasswordCorrect(id, password);
    }
}
