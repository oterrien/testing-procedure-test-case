package com.test.infra.user;

import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends org.springframework.data.jpa.repository.JpaRepository<UserEntity, Integer> {
}
