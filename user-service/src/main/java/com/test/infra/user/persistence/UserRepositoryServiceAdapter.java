package com.test.infra.user.persistence;

import com.test.domain.user.spi.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRepositoryServiceAdapter implements IUserRepository<UserEntity> {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Override
    public Optional<UserEntity> find(long id) {
        return Optional.ofNullable(userJpaRepository.findOne(id));
    }


    @Override
    public List<UserEntity> findAll() {
        return userJpaRepository.findAll();
    }


    @Override
    public long create(UserEntity userEntity) {

        userEntity.setId(0);
        return userJpaRepository.save(userEntity).getId();
    }

    @Override
    public void update(long id, UserEntity userEntity) {
        userEntity.setId(id);
        userJpaRepository.save(userEntity);
    }

    @Override
    public void delete(long id) {
        find(id).ifPresent(p -> userJpaRepository.delete(p.getId()));
    }
}
