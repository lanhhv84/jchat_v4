package com.example.websocketdemo.db;

import com.example.websocketdemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDB extends JpaRepository<User, Integer> {

    @Query(value = "SELECT COUNT(*) FROM user_data WHERE u_username=?1 and u_password=?2", nativeQuery = true)
    int login(String username, String password);

    @Query(value = "SELECT COUNT(*) FROM user_data WHERE u_username=?1", nativeQuery = true)
    int existsUserByUsername(String username);
}
