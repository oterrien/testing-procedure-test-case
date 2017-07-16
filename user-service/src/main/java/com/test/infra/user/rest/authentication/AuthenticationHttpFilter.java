package com.test.infra.user.rest.authentication;

import com.test.domain.common.JSonUtils;
import com.test.domain.user.api.exception.UserActionNotAuthorizedException;
import com.test.infra.user.persistence.PasswordEntity;
import com.test.infra.user.persistence.UserEntity;
import com.test.infra.user.persistence.UserJpaRepository;
import com.test.infra.user.rest.UserRestControllerAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

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
        // no need
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        if (httpServletRequest.getRequestURI().startsWith("/api/v1/users")) {
            try {
                Optional<String> sessionId = Optional.empty();

                Optional<String> authorization = Optional.ofNullable(httpServletRequest.getHeader("Authorization"));
                if (authorization.isPresent()) {

                    String[] baseAuth = new String(Base64.getDecoder().decode(authorization.get().replaceAll("Basic ", ""))).split(":");
                    if (baseAuth.length != 2) {
                        throw new UserActionNotAuthorizedException("Bad credentials");
                    }
                    String login = baseAuth[0];
                    String password = baseAuth[1];

                    Optional<UserEntity> user = Optional.ofNullable(userJpaRepository.findByLogin(login)).
                            filter(u -> u.getPassword().compareTo(new PasswordEntity(password)) == 0);
                    if (user.isPresent()) {
                        sessionId = Optional.of(sessionProviderService.newSessionId());
                        sessionProviderService.put(sessionId.get(), user.get());
                    } else {
                        throw new UserActionNotAuthorizedException("Bad credentials");
                    }
                }

                if (!sessionId.isPresent()) {
                    sessionId = Optional.ofNullable(httpServletRequest.getHeader("session-token"));
                    if (sessionId.isPresent() && !sessionProviderService.get(sessionId.get()).isPresent()) {
                        throw new UserActionNotAuthorizedException("Invalid or obsolete 'session-token'");
                    }
                }

                if (sessionId.isPresent()) {
                    userSessionProviderService.setSessionId(sessionId.get());
                    httpServletResponse.addHeader("session-token", sessionId.get());
                    sessionProviderService.renew(sessionId.get());
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
                } else {
                    throw new UserActionNotAuthorizedException("Either 'Authorization' or 'session-token' are required");
                }
            } catch (UserActionNotAuthorizedException e) {

                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                httpServletResponse.getWriter().write(JSonUtils.serializeToJson(new UserRestControllerAdvice.Error(e.getMessage())));
            }
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    @Override
    public void destroy() {
        // no need
    }
}
