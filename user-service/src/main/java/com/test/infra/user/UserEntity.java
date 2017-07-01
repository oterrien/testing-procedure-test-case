package com.test.infra.user;

import com.test.domain.user.api.IUser;
import com.test.domain.user.api.UserRole;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@javax.persistence.Entity
@Table(name="USERS")
public class UserEntity implements IUser {

    @Id
    @GeneratedValue
    @Column(name="ID")
    private int id;

    @Column(name="LOGIN")
    private String login;

    @NotNull
    @Column(name="PASSWORD")
    private String password;

    @NotNull
    @Column(name="ROLE")
    private UserRole role;
}

