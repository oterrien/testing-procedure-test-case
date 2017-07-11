package com.test.infra.user.persistence;

import com.test.domain.user.api.IPassword;
import com.test.domain.user.api.IUser;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "USERS")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements IUser, Serializable {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private int id;

    @Column(name = "LOGIN", unique = true, nullable=false)
    private String login;

    @Embedded
    private PasswordEntity password;

    @Column(name = "ROLE", nullable=false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public void setPassword(IPassword password) {
        this.password = (PasswordEntity)password;
    }
}

