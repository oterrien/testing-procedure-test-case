package com.test;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class User {

    private int id;
    private String login;
    private String password;
    private Role role;

   public enum Role {
       ADMIN, CLIENT, ADVISOR;
    }
}
