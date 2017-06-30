package com.test;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class User {

    private int id;
    private final String login;
    private String password;
    private Role role;

   public enum Role {
       ADMIN, CLIENT, ADVISOR;
    }
}
