package com.test.infra.user.service;

import com.test.domain.user.api.IUser;
import com.test.domain.user.api.UserRole;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "USERS")
public class UserEntity implements IUser {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private int id;

    @Column(name = "LOGIN", unique = true)
    private String login;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "ROLE")
    @Enumerated(EnumType.STRING)
    private UserRole role;
}

