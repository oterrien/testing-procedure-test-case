package com.test.domain.user;

import com.test.domain.user.api.IUser;
import com.test.domain.user.api.UserRole;
import lombok.Data;

@Data
public class User implements IUser {

    private int id;
    private String login;
    private String password;
    private UserRole role;
}

