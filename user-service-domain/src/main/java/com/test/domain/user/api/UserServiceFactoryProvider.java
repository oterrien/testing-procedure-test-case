package com.test.domain.user.api;

import com.test.domain.user.business.UserServiceFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserServiceFactoryProvider {

    @Getter
    private static final UserServiceFactoryProvider Instance = new UserServiceFactoryProvider();

    @Getter
    private UserServiceFactory userServiceFactory = UserServiceFactory.getInstance();
}
