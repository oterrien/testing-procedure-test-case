package com.test.infra.user.rest;

import com.test.infra.user.service.UserEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserMapperService {

    public UserPayload convert(Optional<UserEntity> entity) {

        return entity.
                map(this::convert).
                orElseThrow(NotFoundException::new);
    }

    public UserPayload convert(UserEntity entity) {

        UserPayload payload = new UserPayload();
        payload.setId(entity.getId());
        payload.setLogin(entity.getLogin());
        payload.setPassword(entity.getPassword());
        payload.setRole(entity.getRole());
        return payload;
    }

    public UserEntity convert(UserPayload payload) {

        UserEntity entity = new UserEntity();
        entity.setId(payload.getId());
        entity.setLogin(payload.getLogin());
        entity.setPassword(payload.getPassword());
        entity.setRole(payload.getRole());
        return entity;
    }

    public class NotFoundException extends RuntimeException {

    }
}
