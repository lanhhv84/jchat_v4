package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.User;
import com.example.websocketdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    public ResponseEntity<?> ok(boolean value) {
        HashMap<String, Boolean> values = new HashMap<>();
        values.put("value", value);
        return ResponseEntity.ok(values);
    }

    @RequestMapping("/add")
    public ResponseEntity<?> add(@RequestParam("username") String username,
                                 @RequestParam("password") String password) {
        if (userService.existsByUserName(username)) {
            return ok(false);
        }
        else {
            User user = new User(username, password);
            userService.add(user);
            return ok(true);
        }
    }

    @RequestMapping("/login")
    public ResponseEntity<?> login(@RequestParam("username") String username,
                                   @RequestParam("password") String password) {

        if (userService.login(username, password)) {
            return ok(true);
        }
        else {
            return ok(false);
        }

    }
}
