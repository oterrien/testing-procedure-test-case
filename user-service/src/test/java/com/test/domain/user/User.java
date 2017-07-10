package com.test.domain.user;

import com.test.domain.user.api.IPassword;
import com.test.domain.user.api.IUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements IUser, Cloneable {

    private int id;
    private String login;
    private IPassword password;
    private Role role;

    public User(String login, IPassword password, Role role) {
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



