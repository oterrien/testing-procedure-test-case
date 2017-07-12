package com.test.infra.user.persistence;

import com.test.domain.user.api.model.Role;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@EqualsAndHashCode(exclude = "user")
@ToString(exclude = "user")
@Entity
@Table(name = "USER_ROLES")
@NoArgsConstructor
public class RoleEntity implements Serializable{

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private int id;

    @Column(name = "ROLE", nullable=false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    public RoleEntity(Role role, UserEntity user){
        this.role = role;
        this.user = user;
    }
}
