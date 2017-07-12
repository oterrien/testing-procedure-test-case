package com.test.infra.user.persistence;

import com.test.domain.user.api.model.IPassword;
import com.test.domain.user.api.model.IUser;
import com.test.domain.user.api.model.Role;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "USERS")
@NoArgsConstructor
public class UserEntity implements IUser, Serializable {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private int id;

    @Column(name = "LOGIN", unique = true, nullable=false)
    private String login;

    @Embedded
    private PasswordEntity password;

    @Override
    public void setPassword(IPassword password) {
        this.password = (PasswordEntity)password;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<RoleEntity> roleEntities;

    public Set<Role> getRoles(){
        return roleEntities.stream().map(RoleEntity::getRole).collect(Collectors.toSet());
    }
}

