package com.example.websocketdemo.model;

import javax.persistence.*;

@Entity
@Table(name = "user_data")
public class User {

    public User() {
        this("", "");
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true)
    private int id;


    @Column(name = "u_username", unique = true, length = 32)
    private String username;

    @Column(name = "u_password", unique = true, length = 32)
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
