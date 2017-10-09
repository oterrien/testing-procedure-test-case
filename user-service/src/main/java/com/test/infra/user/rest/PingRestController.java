package com.test.infra.user.rest;

import com.test.domain.user.spi.IUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/test")
public class PingRestController {

    @CrossOrigin
    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String ping() {
        return "OK";
    }

    private static final List<UserPayload> users = Arrays.asList(new UserPayload(1, "olivier", new PasswordPayload("olivpassword", false), Collections.singleton(IUser.Role.ADMIN)),
            new UserPayload(2, "maryline", new PasswordPayload("yuyu", false), Stream.of(IUser.Role.CLIENT, IUser.Role.ADVISOR).collect(Collectors.toSet())));

    @CrossOrigin
    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserPayload> testUsers() {
        return users;
    }

    @CrossOrigin
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPayload testUser(@PathVariable("id") int id) {
        return users.stream().filter(p -> p.getId() == id).findAny().orElse(null);
    }
}
