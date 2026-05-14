package com.sankalp.prototype.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/public/hello")
    public String publicRoute() {
        return "Welcome anyone can be here";
    }

    @GetMapping("/api/dancefloor")
    public String userRoute() {
        return "Welcome to dance floor! you are logged in";
    }

    @GetMapping("/api/vault")
    public String adminRoute() {
        return "Welcome to vault! you are admin";
    }
}
