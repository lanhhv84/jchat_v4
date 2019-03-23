package com.example.websocketdemo.service;

import com.example.websocketdemo.db.UserDB;
import com.example.websocketdemo.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService extends IService<User, UserDB> {

    public User findOne(String username) {
        return db.findByUsername(username);
    }

    public boolean login(String username, String password) {
        return db.login(username, password) > 0;
    }

    public boolean existsByUserName(String username) {
        return db.existsUserByUsername(username) > 0;
    }
}
