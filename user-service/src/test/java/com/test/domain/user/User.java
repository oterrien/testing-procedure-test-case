package com.test.domain.user;

import com.test.domain.user.api.IUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import static com.test.domain.user.api.IUser.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements IUser, Cloneable {

    private int id;
    private String login;
    private String password;
    private Role role;

    public User(String login, String password, Role role){
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

