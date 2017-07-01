package com.test.infra.user;

import com.test.domain.user.api.IUserService;
import com.test.domain.user.business.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS) // This bean is instantiated for each new request
@Slf4j
public class UserServiceAdapter implements IUserService<UserEntity> {

    private IUserService<UserEntity> userService;

    public UserServiceAdapter(@Autowired UserRepositoryServiceAdapter repositoryServiceAdapter) {
        log.warn("init UserService");
        userService = new UserService<>(repositoryServiceAdapter);
    }

    @Override
    public Optional<UserEntity> get(int id) {
        return userService.get(id);
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
    public void resetPassword(int id, String newPassword) {

    }
}
