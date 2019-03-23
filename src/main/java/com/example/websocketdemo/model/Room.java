package com.example.websocketdemo.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "room_info")
public class Room {

    @Id
    @Column(name = "r_id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "room_name")
    private String name;

    @Column(name = "room_password")
    private String password;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_room",
            joinColumns = {@JoinColumn(name = "room_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<User> users = new HashSet<>();

    @OneToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private Key symmetricKey;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Key getSymmetricKey() {
        return symmetricKey;
    }

    public void setSymmetricKey(Key symmetricKey) {
        this.symmetricKey = symmetricKey;
    }
}
