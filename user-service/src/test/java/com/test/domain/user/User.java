package com.test.domain.user;

import com.test.domain.user.api.IUser;
import lombok.Data;

@Data
public class User implements IUser {

    private int id;
    private String login;
    private String password;
    private Role role;
}

