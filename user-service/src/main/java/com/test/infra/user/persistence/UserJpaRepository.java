package com.test.infra.user.persistence;

import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends org.springframework.data.jpa.repository.JpaRepository<UserEntity, Integer> {

    UserEntity findByLogin(String login);
}
