package com.test.domain.user.api.model;

import java.util.Set;

public interface IUser {

    int getId();

    void setId(int id);

    String getLogin();

    void setLogin(String login);

    IPassword getPassword();

    void setPassword(IPassword password);

    default boolean isSamePassword(IPassword password){
        return getPassword().compareTo(password) == 0;
    }

    Set<Role> getRoles();

    default void addRole(Role role){
        getRoles().add(role);
    }

    default void removeRole(Role role){
        getRoles().remove(role);
    }

    default boolean hasRole(Role role) {
        return getRoles().contains(role);
    }

}

