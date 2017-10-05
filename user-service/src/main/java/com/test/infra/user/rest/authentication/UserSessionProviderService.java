package com.test.infra.user.rest.authentication;

import com.test.domain.user.spi.IUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

@Service
@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSessionProviderService<T extends IUser> {

    @Autowired
    private SessionProviderService<T> sessionProviderService;

    @Getter
    @Setter
    private String sessionId;

    public Optional<T> getUser() {
        return sessionProviderService.get(sessionId);
    }
}
