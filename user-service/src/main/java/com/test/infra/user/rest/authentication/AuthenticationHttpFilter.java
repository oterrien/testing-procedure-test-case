package com.test.infra.user.rest.authentication;

import com.test.domain.user.business.UserServiceWithAuthorization;
import com.test.infra.user.persistence.UserEntity;
import com.test.infra.user.persistence.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Configuration
@Slf4j
public class AuthenticationHttpFilter implements Filter {

    private UserSessionProviderService<UserEntity> userSessionProviderService;

    private SessionProviderService<UserEntity> sessionProviderService;

    private UserJpaRepository userJpaRepository;

    @Autowired
    public AuthenticationHttpFilter(UserSessionProviderService<UserEntity> userSessionProviderService,
                                    SessionProviderService<UserEntity> sessionProviderService,
                                    UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
        this.userSessionProviderService = userSessionProviderService;
        this.sessionProviderService = sessionProviderService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        Optional<String> sessionId = Optional.empty();

        Optional<String> authorization = Optional.ofNullable(httpServletRequest.getHeader("Authorization"));
        if (authorization.isPresent()) {

            String[] baseAuth = new String(Base64.getDecoder().decode(authorization.get().replaceAll("Basic ", ""))).split(":");
            String login = baseAuth[0];
            String password = baseAuth[1];

            Optional<UserEntity> user = Optional.ofNullable(userJpaRepository.findByLogin(login)).filter(u -> u.getPassword().equals(password));
            if (user.isPresent()) {
                sessionId = Optional.of(UUID.randomUUID().toString());
                sessionProviderService.put(sessionId.get(), user);
            } else {
                throw new UserServiceWithAuthorization.NotAuthorizedException("Bad credentials");
            }
        }

        if (!sessionId.isPresent()) {
            sessionId = Optional.ofNullable(httpServletRequest.getHeader("session-token"));
            if (sessionId.isPresent()) {
                if (!sessionProviderService.get(sessionId.get()).isPresent()) {
                    throw new UserServiceWithAuthorization.NotAuthorizedException("Invalid or obsolete 'session-token'");
                }
            }
        }

        if (sessionId.isPresent()) {
            userSessionProviderService.setSessionId(sessionId.get());
            httpServletResponse.addHeader("session-token", sessionId.get());
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            throw new UserServiceWithAuthorization.NotAuthorizedException("Either 'Authorization' or 'session-token' are required");
        }
    }

    @Override
    public void destroy() {

    }
}
