package com.test.domain.user.business;

import com.test.domain.user.api.IUserService;
import com.test.domain.user.api.UserServiceFactoryProvider;
import com.test.domain.user.spi.IUser;
import com.test.domain.user.spi.IUserRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserServiceFactory {

    @Getter
    private static final UserServiceFactory Instance = new UserServiceFactory();

    public <T extends IUser> IUserService<T> create(IUserRepository<T> repository, T user) {
        return new UserAuthorizationService<>(new UserEncodingService<>(new UserService<>(repository)), user);
    }
}
