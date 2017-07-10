package com.test.domain.user.api;

public interface IUser {

    int getId();

    void setId(int id);

    String getLogin();

    void setLogin(String login);

   IPassword getPassword();

   void setPassword(IPassword password);

    Role getRole();

    void setRole(Role role);

    enum Role {
        ADMIN, CLIENT, ADVISOR;
    }
}

