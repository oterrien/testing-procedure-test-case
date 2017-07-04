package com.test.infra.user.service.repository;

import com.test.domain.user.api.IUser;
import lombok.Data;

import javax.persistence.*;
import static com.test.domain.user.api.IUser.Role;

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
    private Role role;
}

