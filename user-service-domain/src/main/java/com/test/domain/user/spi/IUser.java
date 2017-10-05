package com.test.domain.user.spi;

import java.util.Set;

public interface IUser extends Comparable<IUser> {

    long getId();

    void setId(long id);

    String getLogin();

    IPassword getPassword();

    void setPassword(IPassword password);

    default boolean isSamePassword(IPassword password) {
        return getPassword().compareTo(password) == 0;
    }

    Set<Role> getRoles();

    default void addRole(Role role) {
        getRoles().add(role);
    }

    default void removeRole(Role role) {
        getRoles().remove(role);
    }

    default boolean hasRole(Role role) {
        return getRoles().contains(role);
    }

    @Override
    default int compareTo(IUser o) {
        return this.getLogin().compareTo(o.getLogin());
    }

    enum Role {
        ADMIN, CLIENT, ADVISOR;
    }
}
