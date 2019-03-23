package com.example.websocketdemo.service;

import com.example.websocketdemo.db.KeyDB;
import com.example.websocketdemo.model.Key;
import org.springframework.stereotype.Service;

@Service
public class KeyService extends IService<Key, KeyDB> {
    public Key findLastPublicByUserId(int userId) {
        return db.findLast(userId, true);
    }

    public Key findLastPrivateByUserId(int userId) {
        return db.findLast(userId, false);
    }
}
