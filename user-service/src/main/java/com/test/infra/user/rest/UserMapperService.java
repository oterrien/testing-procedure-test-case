package com.test.infra.user.rest;

import com.test.infra.user.persistence.PasswordEntity;
import com.test.infra.user.persistence.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserMapperService {

    public List<UserPayload> convert(List<UserEntity> entity) {

        return entity.stream().
                map(this::convert).
                collect(Collectors.toList());
    }

    public UserPayload convert(Optional<UserEntity> entity) {

        return entity.
                map(this::convert).
                orElseThrow(NotFoundException::new);
    }

    public UserPayload convert(UserEntity entity) {

        UserPayload payload = new UserPayload();
        payload.setId(entity.getId());
        payload.setLogin(entity.getLogin());
        payload.setPassword(convert(entity.getPassword()));
        payload.setRole(entity.getRole());
        return payload;
    }

    public PasswordPayload convert(PasswordEntity entity) {
        PasswordPayload payload = new PasswordPayload();
        payload.setValue(entity.getValue());
        payload.setEncoded(entity.isEncoded());
        return payload;
    }

    public UserEntity convert(UserPayload payload) {

        UserEntity entity = new UserEntity();
        entity.setId(payload.getId());
        entity.setLogin(payload.getLogin());
        entity.setPassword(convert(payload.getPassword()));
        entity.setRole(payload.getRole());
        return entity;
    }

    public PasswordEntity convert(PasswordPayload payload) {
        PasswordEntity entity = new PasswordEntity();
        entity.setValue(payload.getValue());
        entity.setEncoded(payload.isEncoded());
        return entity;
    }

    public class NotFoundException extends RuntimeException {

    }
}
