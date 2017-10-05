package com.test.infra.user.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ping")
public class PingRestController {

    @CrossOrigin
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String ping() {
        return "OK";
    }

}
