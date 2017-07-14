package com.test.domain.user;

import com.test.domain.user.api.model.IPassword;
import com.test.domain.user.api.model.IUser;
import com.test.domain.user.api.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class User implements IUser, Cloneable {

    private int id;
    private final String login;
    private IPassword password;
    private final Set<Role> roles = new HashSet<>();

    public User(String login){
        this.login = login;
    }

    public User(String login, String password, Role role, Role... otherRoles){
        this(login, new Password(password, false), role, otherRoles);
    }

    public User(String login, IPassword password, Role role, Role... otherRoles) {
        this(login);
        setPassword(password);
        roles.add(role);
        if (otherRoles.length>0){
            roles.addAll(Arrays.asList(otherRoles));
        }
    }

    @Override
    protected User clone() {
        try {
            return (User) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Password implements IPassword {

        private String value;
        private boolean encoded = false;

        public Password(String value){
            this.value = value;
        }
    }
}



