package com.test.domain.user;

import org.junit.Test;

public class Encode {

    @Test
    public void encode(){

        System.out.println(new User.Password("password").encoded());
    }
}
