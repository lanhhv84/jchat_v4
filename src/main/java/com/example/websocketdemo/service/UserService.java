package com.example.websocketdemo.service;

import com.example.websocketdemo.db.UserDB;
import com.example.websocketdemo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserDB db;
    public void add(User obj) {
        db.save(obj);
    }
    public void delete(User obj) { db.delete(obj); }
    public void delete(int id) { db.deleteById(id); }
    public User findOne(int id) { return db.getOne(id); }
    public List<User> findAll() {
        return db.findAll();
    }
    public boolean exists(int id) {return db.existsById(id); }
    public boolean login(String username, String password) {return db.login(username, password) > 0; }
    public boolean existsByUserName(String username) {return db.existsUserByUsername(username) > 0; }
}
