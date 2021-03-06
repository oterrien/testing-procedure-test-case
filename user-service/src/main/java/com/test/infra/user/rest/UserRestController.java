package com.test.infra.user.rest;

import com.test.infra.user.persistence.UserEntity;
import com.test.infra.user.service.UserServiceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.test.domain.user.spi.IUser.Role;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@Validated
public class UserRestController {

    @Autowired
    private UserMapperService userMapperService;

    @Autowired
    private UserServiceAdapter userService;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPayload create(@Valid @RequestBody UserPayload userPayload) {
        UserEntity userEntity = userMapperService.convert(userPayload);
        long id = userService.create(userEntity);
        return userMapperService.convert(userService.get(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPayload read(@PathVariable("id") int id) {
        return userMapperService.convert(userService.get(id));
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserPayload> read() {
        return userMapperService.convert(userService.getAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPayload update(@PathVariable("id") int id, @Valid @RequestBody UserPayload userPayload) {
        UserEntity userEntity = userMapperService.convert(userPayload);
        userService.update(id, userEntity);
        return userMapperService.convert(userService.get(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        userService.delete(id);
    }

    @RequestMapping(value = "/{id}/password", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(@PathVariable("id") int id, @Valid @RequestBody PasswordPayload newPassword) {
        userService.resetPassword(id, userMapperService.convert(newPassword));
    }

    @RequestMapping(value = "/{id}/password", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public boolean checkPassword(@PathVariable("id") int id, @Valid @ModelAttribute PasswordPayload password) {
        return userService.isPasswordCorrect(id, userMapperService.convert(password));
    }

    @RequestMapping(value = "/{id}/role", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void addRole(@PathVariable("id") int id, @RequestParam Role role) {
        userService.addRole(id, role);
    }

    @RequestMapping(value = "/{id}/role", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void removeRole(@PathVariable("id") int id, @RequestParam Role role) {
        userService.removeRole(id, role);
    }
}
