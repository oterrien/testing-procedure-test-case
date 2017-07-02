package com.test.domain.user;

import com.test.domain.user.api.IUser;
import com.test.domain.user.api.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements IUser, Cloneable {

    private int id;
    private String login;
    private String password;
    private UserRole role;

    public User(String login, String password, UserRole role){
        setLogin(login);
        setPassword(password);
        setRole(role);
    }

    @Override
    protected User clone() {
        try {
            return (User) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

