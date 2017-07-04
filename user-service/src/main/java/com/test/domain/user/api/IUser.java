package com.test.domain.user.api;

public interface IUser {

    int getId();

    void setId(int id);

    String getLogin();

    void setLogin(String login);

    String getPassword();

    void setPassword(String password);

    Role getRole();

    void setRole(Role role);

    enum Role {
        ADMIN, CLIENT, ADVISOR;
    }
}

